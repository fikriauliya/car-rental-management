var IndexReservationController = function($scope, $state, $stateParams, $filter, $timeout, Reservations, Branches, TimezoneConverter, ngTableParams, ngProgress) {
	$scope.reservations = [[]];
	$scope.detailedReservations = [];
	$scope.progress = 0;

	$scope.startProgress = function() {
		if ($scope.progress == 0) ngProgress.start();
		$scope.progress++;
	};

	$scope.endProgress = function() {
		$scope.progress--;
		if ($scope.progress == 0) ngProgress.complete();
	};

	$scope.refreshReservations = function() {
		$scope.startProgress();
		$scope.$parent.branchResolved.promise.then(function(b) {
			Reservations.query({branchId: $stateParams.branchId, startTime: $scope.curStart, endTime: $scope.curEnd}, function(d, h){
	        	$scope.reservations[0] = [];

	        	_.each(d, function(dd) {
	        		dd.startTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);
	        		dd.start = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);

	        		dd.endTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
	        		dd.end = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
	    		});

	        	$scope.detailedReservations = d;

	        	var now = new Date().getTime();

	        	$scope.pendingReturns = _.groupBy(_.filter(d, function(dd) {
	        		if (dd.status == 'STARTED') {
	        			return true;
	        		} else {
	        			return false;
	        		}
	        	}), 'groupId');

	        	$scope.lateReturns = _.filter($scope.pendingReturns, function(dd) {
	        		if (dd[0].end.getTime() < now) {
	        			return true;
	        		} else {
	        			return false;
	        		}
	        	})

	        	$scope.pendingPaybacks = _.groupBy(_.filter(d, function(dd) {
	        		if ((dd.status == 'CANCELED') && (dd.paidAmount > 0)) {
	        			return true;
	        		} else {
	        			return false;
	        		}
	        	}), 'groupId');

	        	$scope.tableParams.reload();

	        	$scope.endProgress();
	        }, function(d, h) {
	        	$scope.endProgress();
	        });
		}, function() {
			$scope.endProgress();
		});
	};

	$scope.refreshCalendar = function(d) {
		var d2 = _.groupBy(d, 'groupId');
    	var res = [];

    	_.each(d2, function(d) {
    		var newRes = {};
    		newRes.title = d[0].customer.user.firstName;
    		newRes.start = d[0].startTime;
    		newRes.end = d[0].endTime;
    		newRes.groupId = d[0].groupId;
    		res.push(newRes);
    	});

    	$scope.reservations[0] = res;
	}

	$scope.tableParams = new ngTableParams(
	{
	    page: 1, count: 10,
	    sorting: {
	        start: 'asc'
	    }
	},
	{
	    total: 0,
	    groupBy: 'groupId',
	    getData: function($defer, params) {
	    	var filteredData = $filter('filter')($scope.detailedReservations, $scope.filterText);
	        var orderedData = params.sorting() ? $filter('orderBy')(filteredData, params.orderBy()) : filteredData;

	        params.total(orderedData.length);

	        $scope.refreshCalendar(orderedData);
	        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
	    }
	});

	$scope.totalPrice = function(reservations) {
		var a = _.reduce(reservations, function(memo, item){ return memo + item.inventoryFee + item.overdueFee; }, 0);
		if (reservations.length > 0 && reservations[0].assignedDriver) { a = a + reservations[0].driverFee; }
		if (reservations.length > 0 && reservations[0].penaltyFee) { a = a + parseFloat(reservations[0].penaltyFee); };
		return a;
	};

	$scope.filterReservation = function(filterText) {
		$scope.filterText = filterText;
		$scope.tableParams.page(1);
		$scope.tableParams.reload();
	};

	$scope.alertOnEventClick = function(calEvent, jsEvent, view) {
		$scope.filterReservation(calEvent.groupId);
    }

	if ($stateParams.branchId) {
		$scope.setSelectedBranch($stateParams.branchId);
		$scope.uiConfig = {
	      calendar:{
	        height: 450,
	        editable: false,
	        header:{
	          left: 'month agendaWeek agendaDay',
	          center: 'title',
	          right: 'today prev,next'
	        },
	        timezone: false,
	        eventClick: $scope.alertOnEventClick,
	        eventDrop: $scope.alertOnDrop,
	        eventResize: $scope.alertOnResize,
	        eventRender: $scope.eventRender,
	        viewRender: function(view, element) {
	            var start = (new Date(view.start._d)).getTime();
	            var end = (new Date(view.end._d)).getTime();

	            $scope.curStart = start;
	            $scope.curEnd = end;

	            $("#myCalendar").fullCalendar('removeEvents');
	            $scope.refreshReservations();
	        }
	      }
	    };
	}
}
angular.module('adminReservationManagementApp').controller('IndexInventoryController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Reservations', 'Branches', 'TimezoneConverter', 'ngTableParams', 'ngProgress', IndexReservationController]);
