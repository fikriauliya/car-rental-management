var IndexCarController = function($scope, $state, $stateParams, $filter, $timeout, Inventories, Images, TimezoneConverter, ngTableParams, $cookieStore) {
	$scope.inventoryFuelTypes = [
 		{id: 'COMPRESSED_NATURAL_GAS', name: 'Compressed natural gas'},
 		{id: 'DIESEL', name: 'Diesel'},
 		{id: 'ALL_ELECTRIC', name: 'All electric'},
      	{id: 'FLEX_FUEL', name: 'Flex fuel'},
      	{id: 'HYBRID', name: 'Hybrid'},
      	{id: 'PLUG_IN_HYBRID', name: 'Plug-in hybrid'}
     ];

	$scope.selectedInventory = {};
	$scope.filterText = {value: "", minPrice: "", maxPrice: ""};
	$scope.carInventories = [];

	if ($stateParams.branchId) {
		$scope.setSelectedBranch($stateParams.branchId);
		$scope.$parent.branchResolved.promise.then(function(b) {
			var opHour = parseInt($scope.selectedBranch.openingHour.substring(0, 2));
			var opMin = parseInt($scope.selectedBranch.openingHour.substring(4, 6));
			var clHour = parseInt($scope.selectedBranch.closingHour.substring(0, 2));
			var clMin = parseInt($scope.selectedBranch.closingHour.substring(4, 6));

			var today = new Date();
			var tomorrow = new Date(today);
			tomorrow.setDate(today.getDate() + 1);
			tomorrow.setHours(opHour, opMin, 0, 0);

			var tomorrowNight = new Date(tomorrow);
			tomorrowNight.setHours(clHour - 1, 59, 59, 999);

			$scope.search = {
				startTime: tomorrow,
				endTime: tomorrowNight
			};
		});
	}

	$scope.resetFilterText = function() {
		$scope.filterText = {value: "", minPrice: "", maxPrice: ""};
	}

	$scope.minPriceComparator = function (actual, expected) {
		if (expected) {
			return actual >= expected;
		}
		return true;
    };

    $scope.maxPriceComparator = function (actual, expected) {
    	if (expected) {
    		return actual <= expected;
    	}
    	return true;
    };

	$scope.carLoaded = false;

	$scope.$watch('search.endTime', function(newVal, oldVal){
		if (newVal != null) {
			var now = new Date();
			if (newVal.getTime() < now.getTime()) {
				$scope.$parent.errors = ["Selecting past date is not allowed"];
				$scope.search.startTime = oldVal;

				$timeout(function() {
					$scope.$parent.errors = [];
				}, 1500);
			}

			if (newVal.getMinutes() == 0) {
				$scope.search.endTime = new Date(newVal.getTime() - 1);
			} else if ($scope.search.endTime.getTime() <= $scope.search.startTime.getTime()) {
				$scope.search.endTime = new Date($scope.search.startTime.getTime() + 24*60*60*1000);
			}
		}
	});

	$scope.$watch('search.startTime', function(newVal, oldVal){
		if (newVal != null) {
			var now = new Date();
			if (newVal.getTime() < now.getTime()) {
				$scope.$parent.errors = ["Selecting past date is not allowed"];
				$scope.search.startTime = oldVal;

				$timeout(function() {
					$scope.$parent.errors = [];
				}, 1500);
			}
		}

		if ($scope.search != null) {
			if ($scope.search.endTime.getTime() <= $scope.search.startTime.getTime()) {
				$scope.search.endTime = new Date($scope.search.startTime.getTime() + 24*60*60*1000);
			}
		}
	});

	var isBetween = function(opHour, opMin, clHour, clMin, h, m) {
		if (h < opHour) return false;
		if ((h == opHour) && (m < opMin)) return false;
		if (h > clHour) return false;
		if ((h == clHour) && (m > clMin)) return false;
		return true;
	};

	$scope.refreshInventories = function() {
		$scope.startProgress();

		$scope.$parent.branchResolved.promise.then(function(b) {
			var opHour = parseInt($scope.selectedBranch.openingHour.substring(0, 2));
			var opMin = parseInt($scope.selectedBranch.openingHour.substring(4, 6));
			var clHour = parseInt($scope.selectedBranch.closingHour.substring(0, 2));
			var clMin = parseInt($scope.selectedBranch.closingHour.substring(4, 6));

			var stHour = $scope.search.startTime.getHours();
			var stMin = $scope.search.startTime.getMinutes();

			var edHour = $scope.search.endTime.getHours();
			var edMin = $scope.search.endTime.getMinutes();

//			console.log(opHour, opMin, clHour, clMin);
//			console.log(stHour, stMin, edHour, edMin);

			var isInOpeningHour = true;

			if ((opHour < clHour) || ((opHour == clHour) && (opMin <= clMin))) {
				isInOpeningHour = isBetween(opHour, opMin, clHour, clMin, stHour, stMin);
				isInOpeningHour = isInOpeningHour && isBetween(opHour, opMin, clHour, clMin, edHour, edMin);
			} else {
				isInOpeningHour = isBetween(opHour, opMin, 23, 59, stHour, stMin) || isBetween(00, 00, clHour, clMin, stHour, stMin);
				isInOpeningHour = isInOpeningHour && (isBetween(opHour, opMin, 23, 59, edHour, edMin) || isBetween(00, 00, clHour, clMin, edHour, edMin));
			}

			if (!isInOpeningHour) {
				$scope.clearNotification();
				$scope.$parent.errors = ["Your selected start/end time is not in office opening hour. Please correct your selection"]

				$scope.carInventories = [];
				$scope.endProgress();
			}
			else {
				Inventories.query({entity: 'car', branchId: $stateParams.branchId,
					startTime: TimezoneConverter.convertToTargetTimeZoneTime($scope.search.startTime, $scope.selectedBranch.timezone),
					endTime: TimezoneConverter.convertToTargetTimeZoneTime($scope.search.endTime, $scope.selectedBranch.timezone)},
					function(d, h){
						$scope.clearNotification();

						$scope.carInventories = d;
						_.each($scope.carInventories, function(d) { d.type = {id: 'car'}});
						_.each($scope.carInventories, function(d) {
							d.fuelType = _.find($scope.inventoryFuelTypes,
									function(d1){
										return d1.id == d.fuelType;
									});
							d.totalPrice = d.price * (($scope.search.endTime.getTime() - $scope.search.startTime.getTime() + 1) / (60 * 60 * 1000));
						});
						_.each($scope.carInventories, function(d) {
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
						$scope.carLoaded = true;

						$scope.searchResult = {
							startTime: $scope.search.startTime,
							endTime: $scope.search.endTime
						};

						$scope.endProgress();
					},
					function(d, h) {
						$scope.clearNotification();
						$scope.$parent.errors = d.data;

						$scope.carInventories = [];
						$scope.carLoaded = true;
						$scope.endProgress();
					}
				);
			};
		}, function() {
			$scope.clearNotification();
			$scope.endProgress();
		});
	};

	$scope.isLoggedIn = isLoggedIn;

	$scope.reserve = function(car) {
		$cookieStore.put('selectedCar', {
			owner: {id: car.owner.id},
			price: car.price,
			id: car.id,
			name: car.name
		});

		$cookieStore.put('driverFee', $scope.selectedBranch.driverFee);
		$cookieStore.put('timezone', $scope.selectedBranch.timezone);
		$cookieStore.put('startTime', TimezoneConverter.convertToTargetTimeZoneTime($scope.search.startTime, $scope.selectedBranch.timezone));
		$cookieStore.put('endTime', TimezoneConverter.convertToTargetTimeZoneTime($scope.search.endTime, $scope.selectedBranch.timezone));
		$cookieStore.put('currencySymbol', $scope.selectedBranch.currencySymbol);

		if (isLoggedIn && !isAdmin) {
			window.location="customers/addonselection.jsf";
		} else {
			window.location="customers/registration.jsf";
		}
	}

//	$scope.refreshInventories();
}
angular.module('reservationManagementApp').controller('IndexCarController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Inventories', 'Images', 'TimezoneConverter', 'ngTableParams', '$cookieStore', IndexCarController]);
