myApp = angular.module('myApp');
myApp.controller('IndexUserController', ['$scope', '$timeout', 'Users', 'Organizations', 'TransferLogs',
  function($scope, $timeout, Users, Organizations, TransferLogs) {
	$scope.newUser = new Users();
	$scope.newUnit = new Organizations();
	$scope.currentPage = 0;
	$scope.totalPage = 1;
	$scope.units = [];
	$scope.unitsControl = {};
	$scope.inEditMode = false;
	$scope.inTransferMode = false;

	$scope.currentTransferInPage = 0;
	$scope.totalTransferInPage = 1;
	$scope.currentTransferOutPage = 0;
	$scope.totalTransferOutPage = 1;
	$scope.currentLeavePage = 0;
	$scope.totalLeavePage = 1;

	$scope.displayNewUserDialog = function() {
		$scope.info = "";
		$scope.errors = "";
		$('.new-employee-modal').modal('show');
	}

	$scope.createUser = function() {
		$scope.newUser.unitId = $scope.selectedUnit.data.id;
		$scope.newUser.$save({},
			function(data, header) {
				$scope.errors = "";
				$scope.info = "User " + $scope.newUser.id + " has been created";

				$scope.refreshUsers(0);
				$scope.newUser = new Users();
			},
			function(data, header) {
				$scope.errors = data.data;
				$scope.info = "";
			}
	   );
	};

	$scope.refreshUsers = function(page) {
		Users.query({unitId: $scope.selectedUnit.data.id, isAttached: true, page: page}, function(data, header) {
			$scope.users = data.users;
			$scope.currentPage = parseInt(data.currentPage);
			$scope.totalPage = parseInt(data.totalPage);
		});
	};

	$scope.nextPage = function() {
		$scope.refreshUsers($scope.currentPage + 1);
	};

	$scope.prevPage = function() {
		$scope.refreshUsers($scope.currentPage - 1);
	};

	$scope.refreshUnitUsers = function() {
		$scope.refreshUsers(0);
		$scope.refreshTransferIns(0);
		$scope.refreshTransferOuts(0);
		$scope.refreshLeaves(0);
	}

	$scope.refreshUnits = function() {
		Organizations.query({}, function(data, header) {
			$scope.units = [data];
			$scope.selectedUnit = data;

			$scope.refreshUnitUsers();

			$timeout(function() {
				$scope.unitsControl.expand_all();
				$scope.unitsControl.select_first_branch();
			}, 500);
		}, function(data, header) {
			$scope.units = [];
			$scope.selectedUnit = null;
		});
	};

	$scope.changeUnit = function(branch) {
		if ($scope.inTransferMode) {
			$scope.toBeTransferedUser.unit.id = branch.data.id;
			Users.update({}, _.omit($scope.toBeTransferedUser, 'inEditMode'),
				function(data, header) {
					$scope.errors = "";
					$scope.info = "User " + $scope.toBeTransferedUser.id + " has been transfered";
					$scope.selectedUnit = branch;

					$scope.refreshUnitUsers();
					$scope.inTransferMode = false;
				},
				function(data, header) {
					$scope.errors = data.data;
					$scope.selectedUnit = branch;

					$scope.refreshUnitUsers();
					$scope.inTransferMode = false;
				}
			);
		} else {
			$scope.selectedUnit = branch;
			$scope.refreshUnitUsers();
		}
	};

	$scope.displayNewUnitDialog = function() {
		$scope.info = "";
		$scope.errors = "";
		$('.new-unit-modal').modal('show');
	};

	$scope.createUnit = function() {
		if ($scope.selectedUnit)
			$scope.newUnit.parentId = $scope.selectedUnit.data.id;
		else
			$scope.newUnit.parentId = -1;

		$scope.newUnit.$save({},
			function(data, header) {
				$scope.errors = "";
				$scope.info = "Unit " + $scope.newUnit.name + " has been created";

				$scope.refreshUnits();
				$scope.newUnit = new Organizations();

				$('.new-unit-modal').modal('hide');
			},
			function(data, header) {
				$scope.errors = data.data;
				$scope.info = "";
			}
	   );
	};

	$scope.deleteUnit = function() {
		if ($scope.selectedUnit) {
			Organizations.remove({id: $scope.selectedUnit.data.id},
				function(data, header) {
					$scope.refreshUnits();
				}
			);
		}
	};

	$scope.editUnit = function(status) {
		$scope.inEditMode = status;
	};

	$scope.updateUnit = function() {
		Organizations.update({}, $scope.selectedUnit.data,
			function(data, header) {
				$scope.selectedUnit.label = $scope.selectedUnit.data.name;
				$scope.editUnit(false);
			},
			function(data, header) {
				$scope.errors = data.data;
				$scope.editUnit(false);
				$scope.refreshUnits();
			}
		);
	};

	$scope.editUser = function(user, status) {
		user.inEditMode = status;
	};

	$scope.updateUser = function(user) {
		Users.update({}, _.omit(user, 'inEditMode'),
			function(data, header) {
				$scope.errors = "";
				$scope.info = "User " + user.id + " updated";
				$scope.editUser(user, false);
			},
			function(data, header) {
				$scope.errors = data.data;
				$scope.editUser(user, false);
				$scope.refreshUsers(0);
			}
		);
	};

	$scope.leaveUser = function(user) {
		if (confirm("Are you sure to set this user as leaved?")) {
			user.attached = false;
			Users.update({}, _.omit(user, 'inEditMode'),
				function(data, header) {
					$scope.errors = "";
					$scope.info = "User " + user.id + " has been set to 'leaved'";
					$scope.refreshUnitUsers();
				},
				function(data, header) {
					$scope.errors = data.data;
					$scope.refreshUnitUsers();
				}
			);
		}
	};

	$scope.transferUser = function(user) {
		$scope.toBeTransferedUser = user;
		$scope.inTransferMode = true;
	};

	$scope.cancelTransferUser = function() {
		$scope.inTransferMode = false;
	};

	$scope.changeRoles = function(user) {
		$scope.info = "";
		$scope.errors = "";

		$scope.currentUserRoles = Users.query({id: user.id}, function(data, header) {
			$scope.toBeRoleChangedUser = user;
			$scope.availableRoles = data.availableRoles;
			$scope.assignedRoles = data.assignedRoles;
			console.log($scope.availableRoles);
			console.log($scope.assignedRoles);
			$('.change-role-modal').modal('show');
		});
	};

	$scope.toggleRoleSelection = function(role) {
		var idx = $scope.assignedRoles.indexOf(role);

		if (idx > -1) {
			$scope.assignedRoles.splice(idx, 1);
		}
		else {
			$scope.assignedRoles.push(role);
		}
	};

	$scope.saveRoles = function() {
		Users.update({id: $scope.toBeRoleChangedUser.id}, $scope.assignedRoles, function(data, header) {
			$('.change-role-modal').modal('hide');
		});
	};

	$scope.refreshTransferIns = function(page) {
		TransferLogs.query({unitId: $scope.selectedUnit.data.id, page: page, type: "in"}, function(data, header) {
			$scope.transferInLogs = data.transferLogs;
			$scope.currentTransferInPage = parseInt(data.currentPage);
			$scope.totalTransferInPage = parseInt(data.totalPage);
		});
	};

	$scope.nextTransferInPage = function() {
		$scope.refreshTransferIns($scope.currentTransferInPage + 1);
	};

	$scope.prevTransferInPage = function() {
		$scope.refreshTransferIns($scope.currentTransferInPage - 1);
	};

	$scope.refreshTransferOuts = function(page) {
		TransferLogs.query({unitId: $scope.selectedUnit.data.id, page: page, type: "out"}, function(data, header) {
			$scope.transferOutLogs = data.transferLogs;
			$scope.currentTransferOutPage = parseInt(data.currentPage);
			$scope.totalTransferOutPage = parseInt(data.totalPage);
		});
	};

	$scope.nextTransferOutPage = function() {
		$scope.refreshTransferOuts($scope.currentTransferInPage + 1);
	};

	$scope.prevTransferOutPage = function() {
		$scope.refreshTransferOuts($scope.currentTransferInPage - 1);
	};

	$scope.refreshLeaves = function(page) {
		Users.query({unitId: $scope.selectedUnit.data.id, isAttached: false, page: page}, function(data, header) {
			$scope.leaveUsers = data.users;
			$scope.currentLeavePage = parseInt(data.currentPage);
			$scope.totalLeavePage = parseInt(data.totalPage);
		});
	};

	$scope.nextLeavePage = function() {
		$scope.refreshLeaves($scope.currentLeavePage + 1);
	};

	$scope.prevLeavePage = function() {
		$scope.refreshLeaves($scope.currentLeavePage - 1);
	};

	$scope.refreshUnits();
  }]
);