var IndexStatisticsController = function($scope, $state, $stateParams, $filter, $timeout, Reservations, Branches, TimezoneConverter, ngTableParams, ngProgress) {
	$scope.$parent.branchResolved.promise.then(function(b) {
		$scope.selectedMonth = new Date();
		$scope.selectedMonth.setDate(1);
		$scope.selectedMonth.setHours(0);
		$scope.selectedMonth.setMinutes(0);
		$scope.selectedMonth.setSeconds(0);
		$scope.selectedMonth.setMilliseconds(0);
	});

	$scope.generateReport = function() {
		var from = TimezoneConverter.convertToTargetTimeZoneTime($scope.selectedMonth, $scope.selectedBranch.timezone);
		var nextMonth = new Date($scope.selectedMonth);
		nextMonth.setMonth(nextMonth.getMonth() + 1);
		var to  = TimezoneConverter.convertToTargetTimeZoneTime(nextMonth, $scope.selectedBranch.timezone);

		Reservations.query({branchId: $stateParams.branchId, startTime: from, endTime: to}, function(d, h){
			var paidReservations = _.filter(d, function(r) { return r.paid; });
			$scope.groupedReservations = _.groupBy(paidReservations, function(r) { return r.groupId; });
			$scope.totalIncome = _.reduce($scope.groupedReservations, function(memo, data) { return memo + $scope.totalPrice(data); }, 0);

			var inventories = _.map(paidReservations, function(r) { return r.inventory; });
			_.each(inventories, function(dd) {
				if ('maxWeight' in dd) dd.type = "baby_seat";
	    		else if ('numOfSeat' in dd) dd.type = "car";
	    		else dd.type = "gps";
			});
			$scope.groupedInventories = _.sortBy(_.groupBy(inventories, function(r) { return r.id; }), function(r) { return -r.length});
		});
	};

	$scope.totalPrice = function(reservations) {
		var a = _.reduce(reservations, function(memo, item){ return memo + item.inventory.price; }, 0);
		if (reservations.length > 0 && reservations[0].assignedDriver) { return a + reservations[0].driverFee; }
		return a;
	};

	$scope.reservationDetailLink = function(branchId, groupId) {
		return baseUrl + basePath + "/reservations/index.jsf#/" + branchId + "/reservations/" + groupId;
	};

	$scope.inventoryDetailLink = function(branchId, inventoryId, entity) {
		return baseUrl + basePath + "/inventories/index.jsf#/" + branchId + "/inventories/" + inventoryId + "/detail?entity=" + entity;
	}

	$scope.setSelectedBranch($stateParams.branchId);
}
angular.module('statisticsApp').controller('IndexStatisticsController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Reservations', 'Branches', 'TimezoneConverter', 'ngTableParams', 'ngProgress', IndexStatisticsController]);
