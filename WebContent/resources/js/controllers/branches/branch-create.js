branchManagementApp = angular.module('branchManagementApp');
branchManagementApp.controller('CreateBranchController', ['$scope', '$state', 'Branches', 'ngProgress',
  function($scope, $state, Branches, ngProgress) {
	$scope.newBranch = new Branches();

	$scope.createBranch = function() {
		$scope.startProgress();

		$scope.newBranch.$save(
			function(d, h) {
				$scope.clearNotification();
				$scope.info = "New branch " + $scope.newBranch.name + " has been created";
				$scope.newBranch = new Branches();

				$('.branch-modal').modal('hide');
				$scope.refreshBranches();

				$scope.endProgress();
			}, function(d, h) {
				$scope.clearNotification();
				$scope.errors = d.data;

				$scope.endProgress();
			}
		);
	};
  }
]);