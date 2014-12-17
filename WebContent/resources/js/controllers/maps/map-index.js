var IndexMapsController = function($scope, $state, $stateParams, $filter, $timeout, Inventories, Branches, TimezoneConverter, ngTableParams, ngProgress) {

	$scope.refreshInventories = function() {
		$scope.$parent.branchResolved.promise.then(function() {
			console.log("Selected branch: ", $scope.selectedBranch);
			var mapOptions = {
		      center: { lat: $scope.selectedBranch.latitude, lng: $scope.selectedBranch.longitude},
		      zoom: 4
		    };
		    var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
		    var oms = new OverlappingMarkerSpiderfier(map);
		    var iw = new google.maps.InfoWindow();
		    oms.addListener('click', function(marker, event) {
		      iw.setContent(marker.desc);
		      iw.open(map, marker);
		    });

		    oms.addListener('spiderfy', function(markers) {
			  iw.close();
			});

			$scope.inventoryDetailLink = function(branchId, inventoryId, entity) {
				return baseUrl + basePath + "/inventories/index.jsf#/" + branchId + "/inventories/" + inventoryId + "/detail?entity=" + entity;
			};

		    $scope.startProgress();
		    Inventories.query({entity: 'car', branchId: $stateParams.branchId},
				function(d, h){
					$scope.carInventories = d;
					console.log("car", $scope.carInventories);

					_.each($scope.carInventories, function(car) {
						var marker = new google.maps.Marker({
						      position: new google.maps.LatLng(car.latitude, car.longitude),
						      map: map,
						      title: car.name,
						      desc: "<div style='width:200px; height:100px'><b><a target='_blank' href=" + $scope.inventoryDetailLink($stateParams.branchId, car.id, "car") + ">" + car.name + "</a></b>" + "<hr/><b>Location: (" + car.latitude + ", " + car.longitude + ")</b></div>"
						});
						oms.addMarker(marker);
					});
					$scope.endProgress();
				},
				function(d, h) {
					$scope.endProgress();
				}
			);
		});
	};

	$scope.updateLocation = function() {
		Inventories.update({entity: 'car', branchId: $stateParams.branchId, id: $scope.newLocation.inventory.id}, $scope.newLocation.inventory,
			function(d, h) {
				$scope.clearNotification();
				$scope.$parent.info = "Inventory " + $scope.newLocation.inventory.name + " has been updated";

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

	if ($stateParams.branchId) {
		$scope.setSelectedBranch($stateParams.branchId);
		$scope.refreshInventories();
	}
}
angular.module('statisticsApp').controller('IndexMapsController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Inventories', 'Branches', 'TimezoneConverter', 'ngTableParams', 'ngProgress', IndexMapsController]);
