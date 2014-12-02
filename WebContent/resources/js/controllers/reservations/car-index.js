var IndexCarController = function($scope, $state, $stateParams, $filter, $timeout, Inventories, ngTableParams) {
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

	$scope.refreshInventories = function() {
		$scope.startProgress();
		Inventories.query({entity: 'car', branchId: $stateParams.id},
			function(d, h){
				$scope.carInventories = d;
				_.each($scope.carInventories, function(d) { d.type = {id: 'car'}});
				_.each($scope.carInventories, function(d) {
					d.fuelType = _.find($scope.inventoryFuelTypes,
							function(d1){
								return d1.id == d.fuelType;
							})
				});
				$scope.endProgress();
			},
			function(d, h) {
				$scope.endProgress();
			}
		);
	};


	$scope.refreshInventories();
}
angular.module('reservationManagementApp').controller('IndexCarController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Inventories', 'ngTableParams', IndexCarController]);
