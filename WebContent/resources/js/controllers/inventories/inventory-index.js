var IndexInventoryController = function($scope, $state, $stateParams, $filter, $timeout, Inventories, ngTableParams) {
	$scope.newInventory = new Inventories();
	$scope.inventoryTypes = [
	   {id: 'car', name: 'Car'},
	   {id: 'baby_seat', name: 'Baby seat'},
	   {id: 'gps', name: 'GPS'},
    ];
	$scope.inventoryFuelTypes = [
		{id: 'compressed_natural_gas', name: 'Compressed natural gas'},
		{id: 'diesel', name: 'Diesel'},
		{id: 'all_electric', name: 'All electric'},
     	{id: 'flex_fuel', name: 'Flex fuel'},
     	{id: 'hybrid', name: 'Hybrid'},
     	{id: 'plug_in_hybrid', name: 'Plug-in hybrid'}
    ];

	$scope.displayInventoryDialog = function(editMode) {
		$scope.clearNotification();
		$scope.inEditMode = editMode;

		$('.inventory-modal').modal('show');
	};

	$scope.refreshInventories = function() {
		$scope.startProgress();
		Inventories.get({entity: 'car', branchId: $scope.selectedBranch.id},
			function(d, h){
				$scope.inventories = d;
				$timeout(function(){ $scope.inventoryTableParams.reload(); $scope.endProgress();});
			},
			function(d, h) {
				$scope.endProgress();
			}
		);
	};

	$scope.createInventory = function() {
		$scope.startProgress();

		var entity = $scope.newInventory.type.id;
		console.log(entity);
		$scope.newInventory = _.omit($scope.newInventory, 'type');

		$scope.newInventory.$save({entity: entity, branchId: $scope.selectedBranch.id},
			function(d, h) {
				$scope.clearNotification();
				$scope.$parent.info = "New inventory " + $scope.newInventory.name + " has been created";
				$scope.newInventory = new Inventories();

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

	$scope.inventoryTableParams = new ngTableParams(
		{ page: 1, count: 10, sorting: { id: 'asc' }},
		{
		    total: 0,
		    getData: function($defer, params) {
		    	if ($scope.inventories.length > 0) {
			        var orderedData = params.sorting() ? $filter('orderBy')($scope.inventories, params.orderBy()) : $scope.inventories;

			        params.total(orderedData.length);
			        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		    	} else {
		    		$defer.reject();
		    	}
		    }
		});

	$scope.refreshInventories();
}
angular.module('inventoryManagementApp').controller('IndexInventoryController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Inventories', 'ngTableParams', IndexInventoryController]);
