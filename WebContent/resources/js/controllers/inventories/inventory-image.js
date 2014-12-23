var InventoryImagesController = function($scope, $state, $stateParams, Images, Inventories, FileUploader) {
	console.log("+InventoryImagesController");

	$scope.isInventoryOfficer = isInventoryOfficer;

	$scope.refreshImages = function() {
		$scope.startProgress();

		$scope.primaryImage = 0;
		Inventories.get({id: $stateParams.inventoryId, branchId: $stateParams.branchId}, function(d, h) {
			$scope.primaryImage = d.primaryImageId;
			$scope.endProgress();
		}, function(d, h) {
			$scope.endProgress();
		});

		$scope.startProgress();
		Images.query({inventoryId: $stateParams.inventoryId}, function(d, h) {
			$scope.images = _.map(d, function(dd)  {
				return [parseInt(dd), basePath + "/images/" + $stateParams.inventoryId + "/" + dd];
			});
			$scope.endProgress();
		}, function(d, h) {
			$scope.endProgress();
		});
	};

	$scope.isPrimaryImage = function(imageName) {
		return imageName == $scope.primaryImage;
	};

	$scope.makePrimaryImage = function(image) {
		$scope.startProgress();
		Images.update({inventoryId: parseInt($stateParams.inventoryId), imageId: image[0]}, function(d, h){
			$scope.refreshImages();
			$scope.endProgress();
		}, function(d, h) {
			alert("Sorry, there was a problem while setting the primary image");
			$scope.endProgress();
		});
	};

	$scope.deleteImage = function(image) {
		if (confirm("Are you sure want to delete this image?")) {
			$scope.startProgress();
			Images.remove({inventoryId: parseInt($stateParams.inventoryId), imageId: image[0]}, function(d, h) {
				$scope.refreshImages();
				$scope.endProgress();
			}, function(d, h) {
				alert("Sorry, there was a problem while deleting the image");
				$scope.endProgress();
			});
		}
	};

	$scope.uploader = new FileUploader({
		url: basePath + '/UploadServlet',
		formData: [{inventoryId: $stateParams.inventoryId}],
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

    $scope.setSelectedBranch($stateParams.branchId);
	$scope.refreshImages();
	console.log("-InventoryImagesController");
};
angular.module('inventoryManagementApp').controller('InventoryImagesController',
		['$scope', '$state', '$stateParams', 'Images', 'Inventories', 'FileUploader', InventoryImagesController]);
