reservationManagementApp = angular.module('reservationManagementApp');
reservationManagementApp.controller('CreateCustomerController', ['$scope', '$timeout', 'Customers', 'ngProgress', '$cookieStore',
  function($scope, $timeout, Customers, ngProgress, $cookieStore) {
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
		if (isAdmin) {
			$cookieStore.put('reservedForUserId', $scope.newCustomer.id);
		}

		$scope.newCustomer.$save({},
			function(data, header) {
				window.location = "addonselection.jsf";
			},
			function(data, header) {
				$scope.clearNotification();
				$scope.errors = data.data;

				$scope.endProgress();
			}
	   );
	};

	$scope.generatePassword = function() {
		$scope.newCustomer.password = Math.random().toString(36).slice(-10);
		console.log($scope.newCustomer.password);
	};

	$scope.isAdmin = isAdmin;

	if (isLoggedIn && !isAdmin) {
		window.location = "addonselection.jsf";
	}
}]);