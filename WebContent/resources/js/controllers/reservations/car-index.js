var IndexCarController = function($scope, $state, $stateParams, $filter, $timeout, Inventories, TimezoneConverter, ngTableParams, $cookieStore) {
	$scope.inventoryFuelTypes = [
 		{id: 'COMPRESSED_NATURAL_GAS', name: 'Compressed natural gas'},
 		{id: 'DIESEL', name: 'Diesel'},
 		{id: 'ALL_ELECTRIC', name: 'All electric'},
      	{id: 'FLEX_FUEL', name: 'Flex fuel'},
      	{id: 'HYBRID', name: 'Hybrid'},
      	{id: 'PLUG_IN_HYBRID', name: 'Plug-in hybrid'}
     ];

	$scope.selectedInventory = {};

	$scope.carInventories = [];

	var today = new Date();
	var tomorrow = new Date(today);
	tomorrow.setDate(today.getDate() + 1);
	tomorrow.setHours(0, 0, 0, 0);

	var tomorrowNight = new Date(tomorrow);
	tomorrowNight.setHours(23, 59, 59, 999);

	$scope.search = {
		startTime: tomorrow,
		endTime: tomorrowNight
	};

	$scope.carLoaded = false;

	$scope.$watch('search.endTime', function(newVal, oldVal){
		if (newVal.getMinutes() == 0) {
			newVal.setHours(23);
			newVal.setMinutes(59);
			newVal.setSeconds(59);
			newVal.setMilliseconds(999);
		}
	});

	$scope.refreshInventories = function() {
		$scope.startProgress();

		$scope.$parent.branchResolved.promise.then(function(b) {
			Inventories.query({entity: 'car', branchId: $stateParams.id,
				startTime: TimezoneConverter.convertToTargetTimeZoneTime($scope.search.startTime, $scope.selectedBranch.timezone),
				endTime: TimezoneConverter.convertToTargetTimeZoneTime($scope.search.endTime, $scope.selectedBranch.timezone)},
				function(d, h){
					$scope.carInventories = d;
					_.each($scope.carInventories, function(d) { d.type = {id: 'car'}});
					_.each($scope.carInventories, function(d) {
						d.fuelType = _.find($scope.inventoryFuelTypes,
								function(d1){
									return d1.id == d.fuelType;
								})
					});
					$scope.carLoaded = true;
					$scope.endProgress();
				},
				function(d, h) {
					$scope.carLoaded = true;
					$scope.endProgress();
				}
			);
		}, function() {
			$scope.endProgress();
		});
	};

	$scope.isLoggedIn = isLoggedIn;

	$scope.reserve = function(car) {
		$cookieStore.put('selectedCar', car);

		$cookieStore.put('timezone', $scope.selectedBranch.timezone);
		$cookieStore.put('startTime', TimezoneConverter.convertToTargetTimeZoneTime($scope.search.startTime, $scope.selectedBranch.timezone));
		$cookieStore.put('endTime', TimezoneConverter.convertToTargetTimeZoneTime($scope.search.endTime, $scope.selectedBranch.timezone));

		if (!isLoggedIn) {
			window.location="customers/registration.jsf";
		} else {
			window.location="customers/addonselection.jsf";
		}
	}

	$scope.refreshInventories();
}
angular.module('reservationManagementApp').controller('IndexCarController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Inventories', 'TimezoneConverter', 'ngTableParams', '$cookieStore', IndexCarController]);
