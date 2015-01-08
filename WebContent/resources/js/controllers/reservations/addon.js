reservationManagementApp = angular.module('reservationManagementApp');
reservationManagementApp.controller('AddOnController', ['$scope', '$timeout', 'Customers', 'Images', 'TimezoneConverter', 'ngProgress', '$cookieStore', 'Inventories', 'Reservations',
  function($scope, $timeout, Customers, Images, TimezoneConverter, ngProgress, $cookieStore, Inventories, Reservations) {
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
	$scope.timezone = $cookieStore.get('timezone');
	$scope.startTime = TimezoneConverter.convertToLocalTimeZoneTime($cookieStore.get('startTime'), $scope.timezone);
	$scope.endTime = TimezoneConverter.convertToLocalTimeZoneTime($cookieStore.get('endTime'), $scope.timezone);

	$scope.driverFee = $cookieStore.get('driverFee') * (($scope.endTime.getTime() - $scope.startTime.getTime() + 1) / (24 * 60 * 60 * 1000));
	$scope.selectedCar.totalPrice = $scope.selectedCar.price * (($scope.endTime.getTime() - $scope.startTime.getTime() + 1) / (60 * 60 * 1000));

	$scope.selectedInventories = [];

	$scope.babySeatInventories = [];
	$scope.gpsInventories = [];

	$scope.refreshInventories = function() {
		$scope.startProgress();
		Inventories.query({entity: 'gps', branchId: $scope.selectedCar.owner.id, startTime: $scope.startTime.getTime(),
			endTime: $scope.endTime.getTime()},
			function(d, h){
				$scope.gpsInventories = d;
				_.each($scope.gpsInventories, function(d) {
					d.type = {id: 'gps'};
					d.totalPrice = d.price * (($scope.endTime.getTime() - $scope.startTime.getTime() + 1) / (60 * 60 * 1000));
				});
				_.each($scope.gpsInventories, function(d) {
					d.slides = [];

					if (d.primaryImageId != -1) {
						d.slides.push({
							image: baseUrl + basePath + "/images/" + d.id + "/" + [d.primaryImageId]
						});
					}

					Images.query({inventoryId: d.id}, function(images, h) {
						_.each(images, function(curImage)  {
							if (curImage != d.primaryImageId.toString()) {
								d.slides.push({
									image: baseUrl + basePath + "/images/" + d.id + "/" + curImage
								});
							}
						});
					});
				});
				$scope.endProgress();
			},
			function(d, h) {
				$scope.endProgress();
			}
		);

		$scope.startProgress();
		Inventories.query({entity: 'baby_seat', branchId: $scope.selectedCar.owner.id, startTime: $scope.startTime.getTime(),
			endTime: $scope.endTime.getTime()},
			function(d, h){
				$scope.babySeatInventories = d;
				_.each($scope.babySeatInventories, function(d) {
					d.type = {id: 'baby_seat'};
					d.totalPrice = d.price * (($scope.endTime.getTime() - $scope.startTime.getTime() + 1) / (60 * 60 * 1000));
				});
				_.each($scope.babySeatInventories, function(d) {
					d.slides = [];

					if (d.primaryImageId != -1) {
						d.slides.push({
							image: baseUrl + basePath + "/images/" + d.id + "/" + [d.primaryImageId]
						});
					}

					Images.query({inventoryId: d.id}, function(images, h) {
						_.each(images, function(curImage)  {
							if (curImage != d.primaryImageId.toString()) {
								d.slides.push({
									image: baseUrl + basePath + "/images/" + d.id + "/" + curImage
								});
							}
						});
					});
				});
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

	$scope.calculateTotal = function(selectedCar, selectedInventories, driverRequired) {
		var res = selectedCar.totalPrice +
			_.reduce(selectedInventories, function(memo, e) { return memo + e.totalPrice; }, 0) ;
		if (driverRequired) {
			return res + $scope.driverFee;
		}
		return res;
	}

	$scope.createReservation = function(cardPayment) {
		$scope.startProgress();

		$scope.newReservation.inventoryIds = [];
		$scope.newReservation.inventoryIds.push($scope.selectedCar.id);
		$scope.newReservation.inventoryIds = _.union($scope.newReservation.inventoryIds,
			_.map($scope.selectedInventories, function(d) { return d.id }));;
		$scope.newReservation.startTime = TimezoneConverter.convertToTargetTimeZoneTime($scope.startTime, $scope.timezone);
		$scope.newReservation.endTime = TimezoneConverter.convertToTargetTimeZoneTime($scope.endTime, $scope.timezone);
		$scope.newReservation.cardPayment = cardPayment;

		$scope.newReservation.$save(
			function(d, h) {
				$scope.clearNotification();
				console.log(d);
				window.location = "confirmation.jsf#?id=" + d.groupId;

				$scope.endProgress();
			}, function(d, h) {
				$scope.clearNotification();
				$scope.errors = d.data;

				$scope.endProgress();
			}
		);
	};

	if (!isLoggedIn) {
		window.location = "registration.jsf";
	}

	$scope.refreshInventories();
}]);