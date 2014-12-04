var InventoryImagesController = function($scope, $state, $stateParams, Images, Inventories, FileUploader) {
	$scope.refreshImages = function() {
		$scope.primaryImage = 0;
		Inventories.get({id: $stateParams.id, branchId: $stateParams.branchId}, function(d, h) {
			$scope.primaryImage = d.primaryImageId;
		});

		Images.query({inventoryId: $stateParams.id}, function(d, h) {
			$scope.images = _.map(d, function(dd)  {
				return [parseInt(dd), basePath + "/images/" + $stateParams.id + "/" + dd];
			});
		});
	};

	$scope.isPrimaryImage = function(imageName) {
		return imageName == $scope.primaryImage;
	};

	$scope.makePrimaryImage = function(image) {
		Images.update({inventoryId: parseInt($stateParams.id), imageId: image[0]}, function(d, h){
			$scope.refreshImages();
		});
	};

	$scope.uploader = new FileUploader({
		url: basePath + '/UploadServlet',
		formData: [{inventoryId: $stateParams.id}],
		onCompleteAll: function() {
			$scope.refreshImages();
		}
	});
    $scope.uploader.filters.push({
        name: 'imageFilter',
        fn: function(item /*{File|FileLikeObject}*/, options) {
            var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    });
	$scope.refreshImages();
};
angular.module('inventoryManagementApp').controller('InventoryImagesController',
		['$scope', '$state', '$stateParams', 'Images', 'Inventories', 'FileUploader', InventoryImagesController]);
