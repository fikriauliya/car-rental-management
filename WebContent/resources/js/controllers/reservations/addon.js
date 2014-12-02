reservationManagementApp = angular.module('reservationManagementApp');
reservationManagementApp.controller('AddOnController', ['$scope', '$timeout', 'Customers', 'ngProgress', '$cookieStore', 'Inventories', 'Reservations',
  function($scope, $timeout, Customers, ngProgress, $cookieStore, Inventories, Reservations) {
	$scope.errors = [];
	$scope.info = "";
	$scope.progress = 0;
	$scope.newReservation = new Reservations();
	$scope.newReservation.cardExpiryDate = new Date();

	$scope.clearNotification = function() {
		$scope.errors = [];
		$scope.info = "";
	};

	$scope.startProgress = function() {
		if ($scope.progress == 0) ngProgress.start();
		$scope.progress++;
	};

	$scope.endProgress = function() {
		$scope.progress--;
		if ($scope.progress == 0) ngProgress.complete();
	};

	$scope.selectedCar = $cookieStore.get('selectedCar');
	$scope.selectedInventories = [];

	$scope.babySeatInventories = [];
	$scope.gpsInventories = [];

	$scope.refreshInventories = function() {
		$scope.startProgress();
		Inventories.query({entity: 'gps', branchId: $scope.selectedCar.owner.id},
			function(d, h){
				$scope.gpsInventories = d;
				_.each($scope.gpsInventories, function(d) { d.type = {id: 'gps'}});
				$scope.endProgress();
			},
			function(d, h) {
				$scope.endProgress();
			}
		);

		$scope.startProgress();
		Inventories.query({entity: 'baby_seat', branchId: $scope.selectedCar.owner.id},
			function(d, h){
				$scope.babySeatInventories = d;
				_.each($scope.babySeatInventories, function(d) { d.type = {id: 'baby_seat'}});
				$scope.endProgress();
			},
			function(d, h) {
				$scope.endProgress();
			}
		);
	};

	$scope.addInventory = function(inventory) {
		$scope.selectedInventories = _.union($scope.selectedInventories, [inventory]);
	}

	$scope.removeInventory = function(inventory) {
		$scope.selectedInventories = _.without($scope.selectedInventories, inventory);
	}

	$scope.isInSelectedInventory = function(inventory) {
		return _.contains($scope.selectedInventories, inventory);
	}

	$scope.calculateTotal = function(selectedCar, selectedInventories) {
		return selectedCar.price +
			_.reduce(selectedInventories, function(memo, e) { return memo + e.price }, 0);
	}

	$scope.createReservation = function() {
		$scope.startProgress();

		$scope.newReservation.inventoryIds = [];
		$scope.newReservation.inventoryIds.push($scope.selectedCar.id);
		$scope.newReservation.inventoryIds = _.union($scope.newReservation.inventoryIds,
			_.map($scope.selectedInventories, function(d) { return d.id }));;

		console.log($scope.newReservation);
		$scope.newReservation.$save(
			function(d, h) {
				$scope.clearNotification();
				$scope.info = "New reservation " + $scope.newReservation.name + " has been created";

				$scope.endProgress();
			}, function(d, h) {
				$scope.clearNotification();
				$scope.errors = d.data;

				$scope.endProgress();
			}
		);
	};
	$scope.refreshInventories();
}]);