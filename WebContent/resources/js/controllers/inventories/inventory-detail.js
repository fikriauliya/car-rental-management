var InventoryDetailController = function($scope, $state, $stateParams, $filter, $timeout, Inventories, ngTableParams) {
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

	$scope.refreshInventory = function() {
		Inventories.get({branchId: $stateParams.branchId, id: $stateParams.inventoryId}, function(d, h) {
			d.type = $stateParams.entity;
			if (d.type == "car") {
				d.fuelType = {id: d.fuelType};
			}
			$scope.inventory = d;
		});
	};

	if ($stateParams.branchId) {
		$scope.setSelectedBranch($stateParams.branchId);
		$scope.refreshInventory();
	}
};

angular.module('inventoryManagementApp').controller('InventoryDetailController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Inventories', 'ngTableParams', InventoryDetailController]);
