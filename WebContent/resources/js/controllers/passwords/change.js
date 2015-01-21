reservationManagementApp = angular.module('passwordManagementApp');
reservationManagementApp.controller('ChangePasswordController', ['$scope', '$timeout', 'ngProgress', 'Passwords',
  function($scope, $timeout, ngProgress, Passwords) {
	$scope.errors = [];
	$scope.info = "";

	$scope.oldPassword = "";
	$scope.newPassword = "";

	$scope.clearNotification = function() {
		$scope.errors = [];
		$scope.info = "";
	};

	$scope.startProgress = function() {
		if ($scope.progress == 0) ngProgress.start();
		$scope.progress++;
	};

	$scope.endProgress = function() {
		$scope.progress--;
		if ($scope.progress == 0) ngProgress.complete();
	};

	$scope.changePassword = function() {
		Passwords.update({oldPassword: $scope.oldPassword, newPassword: $scope.newPassword}, function(d, h) {
			$scope.clearNotification();
			$scope.info = "Your password has been updated";

			$scope.oldPassword = "";
			$scope.newPassword = "";
		}, function(d, h) {
			$scope.clearNotification();
			$scope.errors =  d.data;
		});
	};
}]);