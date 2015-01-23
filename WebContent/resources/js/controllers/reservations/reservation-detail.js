var ReservationDetailController = function($scope, $state, $stateParams, $filter, $timeout, Reservations, ReservationReschedules, Branches, TimezoneConverter, ngTableParams, ngProgress, Inventories) {
	console.log($stateParams.branchId);
	console.log($stateParams.groupId);
	$scope.reservations = [];
	$scope.inEditScheduleMode = false;

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

				$scope.newSchedule = {
					startTime: d[0].startTime,
					endTime: d[0].endTime
				};

				$scope.endProgress();
			}, function(d, h) {
				$scope.endProgress();
			});
		}, function() {
			$scope.endProgress();
		});
	};

	$scope.$watch('newSchedule.endTime', function(newVal, oldVal){
		if (newVal != null) {
			if (newVal.getMinutes() == 0) {
				$scope.newSchedule.endTime = new Date(newVal.getTime() - 1);
			} else if ($scope.newSchedule.endTime.getTime() <= $scope.newSchedule.startTime.getTime()) {
				$scope.newSchedule.endTime = new Date($scope.newSchedule.startTime.getTime() + 24*60*60*1000);
			}
		}
	});

	$scope.$watch('newSchedule.startTime', function(newVal, oldVal){
		if ($scope.newSchedule != null) {
			if ($scope.newSchedule.endTime.getTime() <= $scope.newSchedule.startTime.getTime()) {
				$scope.newSchedule.endTime = new Date($scope.newSchedule.startTime.getTime() + 24*60*60*1000);
			}
		}
	});

	$scope.confirmReschedule = function() {
		var opHour = parseInt($scope.selectedBranch.openingHour.substring(0, 2));
		var opMin = parseInt($scope.selectedBranch.openingHour.substring(4, 6));
		var clHour = parseInt($scope.selectedBranch.closingHour.substring(0, 2));
		var clMin = parseInt($scope.selectedBranch.closingHour.substring(4, 6));

		var stHour = $scope.newSchedule.startTime.getHours();
		var stMin = $scope.newSchedule.startTime.getMinutes();

		var edHour = $scope.newSchedule.endTime.getHours();
		var edMin = $scope.newSchedule.endTime.getMinutes();

		var isInOpeningHour = true;

		if ((opHour < clHour) || ((opHour == clHour) && (opMin <= clMin))) {
			isInOpeningHour = isBetween(opHour, opMin, clHour, clMin, stHour, stMin);
			isInOpeningHour = isInOpeningHour && isBetween(opHour, opMin, clHour, clMin, edHour, edMin);
		} else {
			isInOpeningHour = isBetween(opHour, opMin, 23, 59, stHour, stMin) || isBetween(00, 00, clHour, clMin, stHour, stMin);
			isInOpeningHour = isInOpeningHour && (isBetween(opHour, opMin, 23, 59, edHour, edMin) || isBetween(00, 00, clHour, clMin, edHour, edMin));
		}

		if (!isInOpeningHour) {
			$scope.clearNotification();
			$scope.$parent.errors = ["Your selected start/end time is not in office opening hour. Please correct your selection"]

			$scope.carInventories = [];
			$scope.endProgress();
		}
		else {
			ReservationReschedules.update({
				groupId: $scope.reservations[0].groupId,
				startTime: TimezoneConverter.convertToTargetTimeZoneTime($scope.newSchedule.startTime, $scope.selectedBranch.timezone),
				endTime: TimezoneConverter.convertToTargetTimeZoneTime($scope.newSchedule.endTime, $scope.selectedBranch.timezone)}, function(d, h) {
					if (d.length == 0) {
						$scope.clearNotification();
						$scope.$parent.info = "The reservation has been rescheduled";
						$scope.refreshReservationDetail();
						$scope.editSchedule(false);
					} else {
						$scope.clearNotification();
						$scope.$parent.errors = _.map(d, function(dd) {
							return dd.name + " is already reserved by another customer. You need to reschedule/cancel that reservation first";
						});
					}
					console.log(d);
				}, function(d, h) {
					console.log(d);
					$scope.clearNotification();
					$scope.$parent.errors = d.data;
				}
			);
		}
	}

	var isBetween = function(opHour, opMin, clHour, clMin, h, m) {
		if (h < opHour) return false;
		if ((h == opHour) && (m < opMin)) return false;
		if (h > clHour) return false;
		if ((h == clHour) && (m > clMin)) return false;
		return true;
	};

	$scope.totalPrice = function(reservations) {
		var a = _.reduce(reservations, function(memo, item){ return memo + item.inventoryFee + item.overdueFee; }, 0);
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

	$scope.markFullyPaid = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "markFullyPaid"}, function(d, h){
			$scope.refreshReservationDetail();
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	};

	$scope.updatePayment = function() {
		Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "updatePayment_" + $scope.reservations[0].paidAmount}, function(d, h){
			$scope.$parent.info = "Payment updated";
			if ($scope.reservations[0].paidAmount - ($scope.totalPrice($scope.reservations)) == 0) {
				if (!$scope.reservations[0].fullyPaid && $scope.reservations[0].status != 'CANCELED') {
					$scope.markFullyPaid();
				}
			} else {
				$scope.refreshReservationDetail();
			}
		}, function(d, h) {
			$scope.refreshReservationDetail();
			alert(d.data);
		});
	}

	$scope.markFullyUnpaid = function() {
		if (confirm("Are you sure want to set this reservation as Unpaid? Please make sure the money has been payed back to the customer first")) {
			Reservations.update({branchId: $stateParams.branchId, groupId: $stateParams.groupId, operation: "markFullyUnpaid"}, function(d, h){
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

	$scope.editSchedule = function(inEditScheduleMode) {
		if (inEditScheduleMode) {
			$scope.newSchedule.startTime = $scope.reservations[0].startTime;
			$scope.newSchedule.endTime = $scope.reservations[0].endTime;
		}
		$scope.inEditScheduleMode = inEditScheduleMode;
	}

	$scope.parentUrl = baseUrl + basePath;
	$scope.setSelectedBranch($stateParams.branchId);
	$scope.refreshReservationDetail();
};

angular.module('adminReservationManagementApp').controller('ReservationDetailController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Reservations', 'ReservationReschedules', 'Branches', 'TimezoneConverter', 'ngTableParams', 'ngProgress', 'Inventories', ReservationDetailController]);
