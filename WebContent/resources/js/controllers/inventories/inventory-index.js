var IndexInventoryController = function($scope, $state, $stateParams, $filter, $timeout, Inventories, ngTableParams) {
	console.log("+IndexInventoryController");

	$scope.initializeNewInventory = function() {
		$scope.newInventory = new Inventories();
		$scope.newInventory.type = {id: 'car'};
		$scope.newInventory.fuelType = {id: 'COMPRESSED_NATURAL_GAS'};
	}
	$scope.highlightInventoryId = $stateParams.highlightInventoryId;

	$scope.initializeNewInventory();
	$scope.selectedInventory = {};

	$scope.carInventories = [];
	$scope.gpsInventories = [];
	$scope.babySeatInventories = [];
	$scope.inventoryTypes = [
	   {id: 'car', name: 'Car'},
	   {id: 'baby_seat', name: 'Baby seat'},
	   {id: 'gps', name: 'GPS'},
    ];
	$scope.inventoryFuelTypes = [
		{id: 'COMPRESSED_NATURAL_GAS', name: 'Compressed natural gas'},
		{id: 'DIESEL', name: 'Diesel'},
		{id: 'ALL_ELECTRIC', name: 'All electric'},
     	{id: 'FLEX_FUEL', name: 'Flex fuel'},
     	{id: 'HYBRID', name: 'Hybrid'},
     	{id: 'PLUG_IN_HYBRID', name: 'Plug-in hybrid'}
    ];

	$scope.displayInventoryDialog = function(editMode, selectedInventory) {
		$scope.clearNotification();
		$scope.inEditMode = editMode;
		$scope.selectedInventory = selectedInventory;

		console.log($scope.selectedInventory);

		$('.inventory-modal').modal('show');
	};

	$scope.refreshInventories = function() {
		$scope.startProgress();
		Inventories.query({entity: 'car', branchId: $stateParams.branchId},
			function(d, h){
				$scope.carInventories = d;
				_.each($scope.carInventories, function(d) { d.type = {id: 'car'}});
				_.each($scope.carInventories, function(d) { d.fuelType = {id: d.fuelType}});
				$timeout(function(){ $scope.carInventoryTableParams.reload(); $scope.endProgress();});
			},
			function(d, h) {
				$scope.endProgress();
			}
		);

		$scope.startProgress();
		Inventories.query({entity: 'gps', branchId: $stateParams.branchId},
			function(d, h){
				$scope.gpsInventories = d;
				_.each($scope.gpsInventories, function(d) { d.type = {id: 'gps'}});
				$timeout(function(){ $scope.gpsInventoryTableParams.reload(); $scope.endProgress();});
			},
			function(d, h) {
				$scope.endProgress();
			}
		);

		$scope.startProgress();
		Inventories.query({entity: 'baby_seat', branchId: $stateParams.branchId},
			function(d, h){
				$scope.babySeatInventories = d;
				_.each($scope.babySeatInventories, function(d) { d.type = {id: 'baby_seat'}});
				$timeout(function(){ $scope.babySeatInventoryTableParams.reload(); $scope.endProgress();});
			},
			function(d, h) {
				$scope.endProgress();
			}
		);
	};

	var cleanUpData = function(inventory) {
		var entity = inventory.type.id;
		var res = _.omit(inventory, 'type');
		if (entity == "car") {
			res.fuelType = inventory.fuelType.id;
		} else {
			res = _.omit(res, 'fuelType');
		}
		return res;
	}

	$scope.createInventory = function() {
		$scope.startProgress();

		var entity = $scope.newInventory.type.id;
		var d = cleanUpData($scope.newInventory);

		d.$save({entity: entity, branchId: $stateParams.branchId},
			function(d, h) {
				$scope.clearNotification();
				$scope.$parent.info = "New inventory " + $scope.newInventory.name + " has been created";
				$scope.initializeNewInventory();

				$('.inventory-modal').modal('hide');
				$scope.refreshInventories();

				$scope.endProgress();
			}, function(d, h) {
				$scope.clearNotification();
				$scope.$parent.errors = d.data;

				$scope.endProgress();
			}
		);
	};

	$scope.updateInventory = function() {
		$scope.startProgress();

		var entity = $scope.selectedInventory.type.id;
		var d = cleanUpData($scope.selectedInventory);

		Inventories.update({entity: entity, branchId: $stateParams.branchId, id: $scope.selectedInventory.id}, d,
			function(d, h) {
				$scope.clearNotification();
				$scope.$parent.info = "Inventory " + $scope.selectedInventory.name + " has been updated";

				$('.inventory-modal').modal('hide');
				$scope.refreshInventories();

				$scope.endProgress();
			},
			function(d, h) {
				$scope.clearNotification();
				$scope.$parent.errors = d.data;

				$scope.endProgress();
			}
		);
	};

	$scope.deleteInventory = function(data) {
		if (confirm("Are you sure want to delete this inventory?")) {
			$scope.startProgress();

			Inventories.remove({branchId: $stateParams.branchId, id: data.id},
				function(d, h) {
					$scope.clearNotification();

					$scope.$parent.info = "Inventory " + data.name + " has been deleted";
					$scope.refreshInventories();

					$scope.endProgress();
				},
				function(d, h) {
					$scope.clearNotification();

					$scope.$parent.errors = d.data;

					$scope.endProgress();
				}
			);
		}
	}

	$scope.carInventoryTableParams = new ngTableParams(
		{ page: 1, count: 10, sorting: { id: 'asc' }},
		{
		    total: 0,
		    getData: function($defer, params) {
		    	if ($scope.carInventories.length > 0) {
			        var orderedData = params.sorting() ? $filter('orderBy')($scope.carInventories, params.orderBy()) : $scope.carInventories;

			        params.total(orderedData.length);
			        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		    	} else {
		    		$defer.reject();
		    	}
		    }
		});

	$scope.babySeatInventoryTableParams = new ngTableParams(
		{ page: 1, count: 10, sorting: { id: 'asc' }},
		{
		    total: 0,
		    getData: function($defer, params) {
		    	if ($scope.babySeatInventories.length > 0) {
			        var orderedData = params.sorting() ? $filter('orderBy')($scope.babySeatInventories, params.orderBy()) : $scope.babySeatInventories;

			        params.total(orderedData.length);
			        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		    	} else {
		    		$defer.reject();
		    	}
		    }
		});

	$scope.gpsInventoryTableParams = new ngTableParams(
		{ page: 1, count: 10, sorting: { id: 'asc' }},
		{
		    total: 0,
		    getData: function($defer, params) {
		    	if ($scope.gpsInventories.length > 0) {
			        var orderedData = params.sorting() ? $filter('orderBy')($scope.gpsInventories, params.orderBy()) : $scope.gpsInventories;

			        params.total(orderedData.length);
			        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		    	} else {
		    		$defer.reject();
		    	}
		    }
		});

	if ($stateParams.branchId) {
		$scope.setSelectedBranch($stateParams.branchId);
		$scope.refreshInventories();
	}

	console.log("-IndexInventoryController");
}
angular.module('inventoryManagementApp').controller('IndexInventoryController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Inventories', 'ngTableParams', IndexInventoryController]);
