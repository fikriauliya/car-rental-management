var InventoryDetailController = function($scope, $state, $stateParams, $filter, $timeout, Inventories, ReservationHistories, ngTableParams) {
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

	$scope.reservationHistories = [];

	$scope.isAdmin = isAdmin;

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
		ReservationHistories.query({inventoryId: $stateParams.inventoryId}, function(d, h) {
			$scope.reservationHistories = d;
			$scope.tableParams.reload();
			$scope.endProgress();
		}, function(d, h) {
			$scope.endProgress();
		});
	};

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
		 'Inventories', 'ReservationHistories', 'ngTableParams', InventoryDetailController]);
