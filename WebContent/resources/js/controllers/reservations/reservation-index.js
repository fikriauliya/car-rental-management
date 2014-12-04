var IndexReservationController = function($scope, $state, $stateParams, $filter, $timeout, Reservations, Branches, TimezoneConverter, ngTableParams) {
	$scope.reservations = [[]];
	$scope.detailedReservations = [];

	$scope.refreshReservations = function() {
		Reservations.query({branchId: $stateParams.id, startTime: $scope.curStart, endTime: $scope.curEnd}, function(d, h){
        	$scope.reservations[0] = [];

        	_.each(d, function(dd) {
        		dd.startTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);
        		dd.start = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);

        		dd.endTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
        		dd.end = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
    		});

        	$scope.detailedReservations = d;

        	var d2 = _.groupBy(d, 'groupId');
        	var res = [];

        	_.each(d2, function(d) {
        		var newRes = {};
        		newRes.title = d[0].customer.user.firstName + " " + d[0].customer.user.lastName;
        		newRes.start = d[0].startTime;
        		newRes.end = d[0].endTime;
        		res.push(newRes);
        	});

        	$scope.reservations[0] = res;
        	$scope.tableParams.reload();

        }, function(d, h) {
        	$scope.clearNotification();
			$scope.errors = d.data;
        });
	};

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
	        var orderedData = params.sorting() ? $filter('orderBy')($scope.detailedReservations, params.orderBy()) : $scope.detailedReservations;

	        params.total(orderedData.length);
	        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
	    }
	});

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
angular.module('adminReservationManagementApp').controller('IndexInventoryController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Reservations', 'Branches', 'TimezoneConverter', 'ngTableParams', IndexReservationController]);
