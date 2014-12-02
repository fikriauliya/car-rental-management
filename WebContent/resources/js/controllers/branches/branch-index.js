var IndexBranchController = function($scope, $state, Branches, ngProgress) {
	$scope.errors = [];
	$scope.info = "";
	$scope.progress = 0;

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

	$scope.startProgress = function() {
		if ($scope.progress == 0) ngProgress.start();
		$scope.progress++;
	};

	$scope.endProgress = function() {
		$scope.progress--;
		if ($scope.progress == 0) ngProgress.complete();
	};

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

	$scope.updateBranch = function() {
		$scope.startProgress();

		Branches.update({id: $scope.selectedBranch.id, branch: $scope.selectedBranch}, $scope.selectedBranch,
			function(d, h) {
				$scope.clearNotification();
				$scope.info = "Branch " + $scope.selectedBranch.name + " has been updated";

				$('.branch-modal').modal('hide');
				$scope.refreshBranches();

				$scope.endProgress();
			},
			function(d, h) {
				$scope.clearNotification();
				$scope.errors = d.data;

				$scope.endProgress();
			}
		);
	};

	$scope.deleteBranch = function() {
		$scope.startProgress();

		Branches.remove({id: $scope.selectedBranch.id},
			function(d, h) {
				$scope.clearNotification();

				$scope.info = "A branch has been deleted";
				$scope.refreshBranches();

				$scope.endProgress();
			},
			function(d, h) {
				$scope.clearNotification();

				$scope.errors = d.data;

				$scope.endProgress();
			}
		);
	};

	$scope.editBranch = function() {
		$scope.inEditMode = true;

		$scope.displayBranchDialog(true);
	};

	$scope.refreshBranches = function() {
		$scope.startProgress();

		Branches.query({}, function(d, h){
			$scope.branches = d;
			if ($scope.branches != null && $scope.branches.length > 0) {
				$scope.changeBranch($scope.branches[0]);
			}

			$scope.endProgress();
		}, function(d, h) {
			$scope.endProgress();
		});
	};

	$scope.changeBranch = function(branch) {
		$scope.selectedBranch = branch;
		$state.go('branch.members', {id: branch.id});
	};

	$scope.refreshBranches();
  }

angular.module('branchManagementApp').controller('IndexBranchController', ['$scope', '$state', 'Branches', 'ngProgress', IndexBranchController]);
angular.module('inventoryManagementApp').controller('IndexBranchController', ['$scope', '$state', 'Branches', 'ngProgress', IndexBranchController]);
angular.module('reservationManagementApp').controller('IndexBranchController', ['$scope', '$state', 'Branches', 'ngProgress', IndexBranchController]);
