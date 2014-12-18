var ReservationDetailController = function($scope, $state, $stateParams, $filter, $timeout, Reservations, Branches, TimezoneConverter, ngTableParams, ngProgress) {
	console.log($stateParams.branchId);
	console.log($stateParams.groupId);
	$scope.reservations = [];

	$scope.refreshReservationDetail = function() {
		$scope.startProgress();
		$scope.$parent.branchResolved.promise.then(function(b) {
			Reservations.query({branchId: $stateParams.branchId, groupId: $stateParams.groupId}, function(d, h) {
				_.each(d, function(dd) {
		    		dd.startTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);
		    		dd.start = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);

		    		dd.endTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
		    		dd.end = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);

		    		// HACK!
		    		if ('maxWeight' in dd.inventory) dd.inventory.type = "baby_seat";
		    		else if ('numOfSeat' in dd.inventory) dd.inventory.type = "car";
		    		else dd.inventory.type = "gps";
				});
				$scope.reservations = d;
				$scope.endProgress();
			}, function(d, h) {
				$scope.endProgress();
			});
		}, function() {
			$scope.endProgress();
		});
	}

	$scope.totalPrice = function(reservations) {
		var a = _.reduce(reservations, function(memo, item){ return memo + item.inventory.price * ((item.endTime.getTime() - item.startTime.getTime() + 1) / (60 * 60 * 1000)); }, 0);
		if (reservations.length > 0 && reservations[0].assignedDriver) { return a + reservations[0].driverFee; }
		return a;
	}

	$scope.calculatePrice = function(reservation) {
		return reservation.inventory.price * ((reservation.endTime.getTime() - reservation.startTime.getTime() + 1) / (60 * 60 * 1000));
	}

	$scope.startRental = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "startRental"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert("You can't start renting on this reservation. Please make sure all inventories reserved are available");
		});
	};

	$scope.finishRental = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "finishRental"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.finishChecking = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "finishChecking"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.cancelRental = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "cancelRental"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.markPaid = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "markPaid"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.parentUrl = baseUrl + basePath;
	$scope.setSelectedBranch($stateParams.branchId);
	$scope.refreshReservationDetail();
};

angular.module('adminReservationManagementApp').controller('ReservationDetailController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Reservations', 'Branches', 'TimezoneConverter', 'ngTableParams', 'ngProgress', ReservationDetailController]);
