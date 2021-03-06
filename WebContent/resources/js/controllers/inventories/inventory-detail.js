var InventoryDetailController = function($scope, $state, $stateParams, $filter, $timeout, Inventories, ReservationHistories, TimezoneConverter, ngTableParams) {
	$scope.inventory = {};
	$scope.inventoryFuelTypes =
	[
 		{id: 'COMPRESSED_NATURAL_GAS', name: 'Compressed natural gas'},
 		{id: 'DIESEL', name: 'Diesel'},
 		{id: 'ALL_ELECTRIC', name: 'All electric'},
      	{id: 'FLEX_FUEL', name: 'Flex fuel'},
      	{id: 'HYBRID', name: 'Hybrid'},
      	{id: 'PLUG_IN_HYBRID', name: 'Plug-in hybrid'}
     ];

	var cleanUpData = function(inventory) {
		var entity = inventory.type;
		var res = _.omit(inventory, 'type');
		if (entity == "car") {
			res.fuelType = inventory.fuelType.id;
		} else {
			res = _.omit(res, 'fuelType');
		}
		return res;
	}

	$scope.reservationHistories = [];

	$scope.isAdmin = isAdmin;
	$scope.isDirector = isDirector;
	$scope.isTechnician = isTechnician;
	$scope.isInventoryOfficer = isInventoryOfficer;

	$scope.refreshInventory = function() {
		$scope.startProgress();
		Inventories.get({branchId: $stateParams.branchId, id: $stateParams.inventoryId}, function(d, h) {
			d.type = $stateParams.entity;
			if (d.type == "car") {
				d.fuelType = {id: d.fuelType};
			}
			$scope.inventory = d;
			$scope.endProgress();
		}, function(d, h) {
			$scope.endProgress();
		});
	};

	$scope.refreshReservationHistories = function() {
		$scope.startProgress();
		$scope.$parent.branchResolved.promise.then(function(b) {
			ReservationHistories.query({inventoryId: $stateParams.inventoryId}, function(d, h) {
				_.each(d, function(dd) {
	        		dd.startTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);
	        		dd.start = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);

	        		dd.endTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
	        		dd.end = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
	    		});

				$scope.reservationHistories = d;
				$scope.tableParams.reload();
				$scope.endProgress();
			}, function(d, h) {
				$scope.endProgress();
			});
		}, function() {
			$scope.endProgress();
		});
	};

	$scope.updateStatus = function(newStatus) {
		var d = cleanUpData($scope.inventory);
		d.status = newStatus;

		$scope.startProgress();
		Inventories.update({entity: $stateParams.entity, branchId: $stateParams.branchId, id: $stateParams.inventoryId}, d,
			function(d, h) {
				$scope.refreshInventory();
				$scope.endProgress();
			},
			function(d, h) {
				$scope.endProgress();
			}
		);
	}
	$scope.tableParams = new ngTableParams(
	{
	    page: 1, count: 10,
	    sorting: {
	        start: 'desc'
	    }
	},
	{
	    total: 0,
	    getData: function($defer, params) {
//	    	var filteredData = $filter('filter')($scope.detailedReservations, $scope.filterText);
	    	var filteredData = $scope.reservationHistories;
	        var orderedData = params.sorting() ? $filter('orderBy')(filteredData, params.orderBy()) : filteredData;

	        params.total(orderedData.length);
	        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
	    }
	});

	if ($stateParams.branchId) {
		$scope.branchId = $stateParams.branchId;
		$scope.baseUrl = baseUrl;
		$scope.basePath = basePath;

		$scope.setSelectedBranch($stateParams.branchId);
		$scope.refreshInventory();
		$scope.refreshReservationHistories();
	}
};

angular.module('inventoryManagementApp').controller('InventoryDetailController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Inventories', 'ReservationHistories', 'TimezoneConverter', 'ngTableParams', InventoryDetailController]);
