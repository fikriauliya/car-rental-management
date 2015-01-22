var ReservationDetailController = function($scope, $state, $stateParams, $filter, $timeout, Reservations, Branches, TimezoneConverter, ngTableParams, ngProgress, Inventories) {
	console.log($stateParams.branchId);
	console.log($stateParams.groupId);
	$scope.reservations = [];

	$scope.isAdmin = isAdmin;

	$scope.refreshReservationDetail = function() {
		$scope.startProgress();
		$scope.$parent.branchResolved.promise.then(function(b) {
			Reservations.query({branchId: $stateParams.branchId, groupId: $stateParams.groupId}, function(d, h) {
				_.each(d, function(dd) {
		    		dd.startTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);
		    		dd.start = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);

		    		dd.endTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
		    		dd.end = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);

		    		// HACK!
		    		if ('maxWeight' in dd.inventory) dd.inventory.type = "baby_seat";
		    		else if ('numOfSeat' in dd.inventory) dd.inventory.type = "car";
		    		else dd.inventory.type = "gps";
				});

				$scope.totalOverdueFee = _.reduce(d, function(memo, dd) { return memo + dd.overdueFee; }, 0);
				$scope.reservations = d;

				$scope.refreshMap();

				$scope.endProgress();
			}, function(d, h) {
				$scope.endProgress();
			});
		}, function() {
			$scope.endProgress();
		});
	}

	$scope.totalPrice = function(reservations) {
		var a = _.reduce(reservations, function(memo, item){ return memo + item.inventoryFee; }, 0);
		if (reservations.length > 0 && reservations[0].assignedDriver) { return a + reservations[0].driverFee; }
		return a;
	}

	$scope.startRental = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "startRental"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.finishRental = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "finishRental"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.finishChecking = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "finishChecking"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.cancelRental = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "cancelRental"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.markPaid = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "markPaid"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.markUnpaid = function() {
		if (confirm("Are you sure want to set this reservation as Unpaid? Please make sure the money has been payed back to the customer first")) {
			Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "markUnpaid"}, function(d, h){
				$scope.refreshReservationDetail();
			}, function(d, h) {
				$scope.refreshReservationDetail();
				alert(d.data);
			});
		}
	};

	$scope.markOverduePaid = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "markOverduePaid"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.markOverdueUnpaid = function() {
		if (confirm("Are you sure want to set this reservation as Unpaid? Please make sure the money has been payed back to the customer first")) {
			Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "markOverdueUnpaid"}, function(d, h){
				$scope.refreshReservationDetail();
			}, function(d, h) {
				$scope.refreshReservationDetail();
				alert(d.data);
			});
		}
	};

	$scope.refreshMap = function() {
		$scope.$parent.branchResolved.promise.then(function() {
			var mapOptions = {
		      center: { lat: $scope.selectedBranch.latitude, lng: $scope.selectedBranch.longitude},
		      zoom: 2
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

		    console.log($scope.reservations);
		    var carReservation = _.find($scope.reservations, function(d) { return d.inventory.type == 'car'});

		    Inventories.get({branchId: $stateParams.branchId, id: carReservation.inventory.id},
				function(car, h){
					var marker = new google.maps.Marker({
					      position: new google.maps.LatLng(car.latitude, car.longitude),
					      map: map,
					      title: car.name,
					      desc: "<div style='width:200px; height:100px'><b><a target='_blank' href=" + $scope.inventoryDetailLink($stateParams.branchId, car.id, "car") + ">" + car.name + "</a></b>" + "<hr/><b>Location: (" + car.latitude + ", " + car.longitude + ")</b></div>"
					});
					oms.addMarker(marker);
					$scope.endProgress();
				},
				function(d, h) {
					$scope.endProgress();
				}
			);
		});
	};

	$scope.parentUrl = baseUrl + basePath;
	$scope.setSelectedBranch($stateParams.branchId);
	$scope.refreshReservationDetail();
};

angular.module('adminReservationManagementApp').controller('ReservationDetailController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Reservations', 'Branches', 'TimezoneConverter', 'ngTableParams', 'ngProgress', 'Inventories', ReservationDetailController]);
