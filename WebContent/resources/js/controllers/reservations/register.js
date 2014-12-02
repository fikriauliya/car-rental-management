reservationManagementApp = angular.module('reservationManagementApp');
reservationManagementApp.controller('CreateCustomerController', ['$scope', '$timeout', 'Customers', 'ngProgress',
  function($scope, $timeout, Customers, ngProgress) {
	$scope.errors = [];
	$scope.info = "";
	$scope.progress = 0;

	$scope.clearNotification = function() {
		$scope.errors = [];
		$scope.info = "";
	};

	$scope.displayBranchDialog = function(editMode) {
		$scope.inEditMode = editMode;
		$scope.clearNotification();

		$('.branch-modal').modal('show');
	};

	$scope.startProgress = function() {
		if ($scope.progress == 0) ngProgress.start();
		$scope.progress++;
	};

	$scope.endProgress = function() {
		$scope.progress--;
		if ($scope.progress == 0) ngProgress.complete();
	};

	$scope.newCustomer = new Customers();
	$scope.newCustomer.birthDate = new Date();

	$scope.createCustomer = function() {
		$scope.startProgress();
		$scope.newCustomer.$save({},
			function(data, header) {
				$scope.clearNotification();
				$scope.info = "User " + $scope.newCustomer.id + " has been created";

				$scope.newCustomer = new Customers();
				$scope.endProgress();
			},
			function(data, header) {
				$scope.clearNotification();
				$scope.errors = data.data;

				$scope.endProgress();
			}
	   );
	};

	if (isLoggedIn) {
		window.location = "addonselection.jsf";
	}
}]);