var InventoryImagesController = function($scope, $state, $stateParams, Images) {
	$scope.refreshImages = function() {
		$scope.images  = Images.query({inventoryId: $stateParams.id});
	};

	$scope.refreshImages();
}
angular.module('InventoryImagesController').controller('IndexInventoryController',
		['$scope', '$state', 'Images', IndexInventoryController]);
