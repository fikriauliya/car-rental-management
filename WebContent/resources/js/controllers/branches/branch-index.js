var IndexBranchController = function($scope, $state, $timeout, Branches, Timezones, ngProgress, $q, $stateParams) {
	console.log("+IndexBranchController");

	$scope.errors = [];
	$scope.info = "";
	$scope.progress = 0;

	$scope.newBranch = new Branches();
	$scope.allTimezones = Timezones.query();
	$scope.selectedBranch = {};

	$scope.branchResolved = $q.defer();
	$scope.newBranch = new Branches();

	$scope.displayCreateNewBranch = $state.current.data.displayCreateNewBranch;

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
		if (confirm("Warning, deleting the branch will make all reservations in this branch inaccessible. Are you sure want to delete?")) {
			$scope.startProgress();

			Branches.remove({id: $scope.selectedBranch.id},
				function(d, h) {
					$scope.clearNotification();

					$scope.info = "A branch has been deleted";
					$scope.refreshBranches();

					$timeout(function() {
						if ($scope.branches.length > 0) {
							$scope.changeBranch($scope.branches[0]);
						}
					}, 1500);

					$scope.endProgress();
				},
				function(d, h) {
					$scope.clearNotification();

					$scope.errors = d.data;

					$scope.endProgress();
				}
			);
		}
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
//				$scope.changeBranch($scope.branches[0]);
				$scope.branchResolved.resolve();
			} else {
				$scope.branchResolved.reject();
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

	$scope.setSelectedBranch = function(branchId) {
		$scope.branchResolved.promise.then(function() {
			var branch = _.find($scope.branches, function(b) { return b.id == branchId; });
			console.log(branchId, " -> ", branch);
			$scope.selectedBranch = branch;
		});
	};

	$scope.refreshBranches();
	console.log("-IndexBranchController");
  }

angular.module('branchManagementApp').controller('IndexBranchController', ['$scope', '$state', '$timeout', 'Branches', 'Timezones', 'ngProgress', '$q', '$stateParams', IndexBranchController]);
angular.module('inventoryManagementApp').controller('IndexBranchController', ['$scope', '$state', '$timeout', 'Branches', 'Timezones', 'ngProgress', '$q', '$stateParams', IndexBranchController]);
angular.module('reservationManagementApp').controller('IndexBranchController', ['$scope', '$state', '$timeout', 'Branches', 'Timezones', 'ngProgress', '$q', '$stateParams', IndexBranchController]);
