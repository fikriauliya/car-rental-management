branchManagementApp = angular.module('branchManagementApp');
branchManagementApp.controller('IndexBranchController', ['$scope', '$state', 'Branches',
  function($scope, $state, Branches) {
	$scope.errors = [];
	$scope.info = "";

	$scope.newBranch = new Branches();

	$scope.clearNotification = function() {
		$scope.errors = [];
		$scope.info = "";
	};

	$scope.displayBranchDialog = function(editMode) {
		$scope.inEditMode = editMode;
		$scope.clearNotification();

		$('.branch-modal').modal('show');
	};

	$scope.createBranch = function() {
		$scope.newBranch.$save(
			function(d, h) {
				$scope.clearNotification();
				$scope.info = "New branch (" + $scope.newBranch.name + ") has been created";
				$scope.newBranch = new Branches();

				$('.branch-modal').modal('hide');
				$scope.refreshBranches();
			}, function(d, h) {
				$scope.clearNotification();
				$scope.errors = d.data;
			}
		);
	};

	$scope.updateBranch = function() {
		Branches.update({id: $scope.selectedBranch.id, branch: $scope.selectedBranch}, $scope.selectedBranch,
			function(d, h) {
				$scope.clearNotification();
				$scope.info = "Branch (" + $scope.selectedBranch.name + ") has been updated";

				$('.branch-modal').modal('hide');
				$scope.refreshBranches();
			},
			function(d, h) {
				$scope.clearNotification();
				$scope.errors = d.data;
			}
		);
	};

	$scope.deleteBranch = function() {
		Branches.remove({id: $scope.selectedBranch.id},
			function(d, h) {
				$scope.clearNotification();

				$scope.info = "A branch has been deleted";
				$scope.refreshBranches();
			},
			function(d, h) {
				$scope.clearNotification();

				$scope.errors = d.data;
			}
		);
	};

	$scope.editBranch = function() {
		$scope.inEditMode = true;

		$scope.displayBranchDialog(true);
	};

	$scope.refreshBranches = function() {
		Branches.query({}, function(d, h){
			$scope.branches = d;
			if ($scope.branches != null && $scope.branches.length > 0) {
				$scope.changeBranch($scope.branches[0]);
			}
		});
	};

	$scope.changeBranch = function(branch) {
		$state.go('branch.members', {id: branch.id});
		$scope.selectedBranch = branch;
	};

	$scope.refreshBranches();
  }
]);