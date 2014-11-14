myApp = angular.module('myApp');
myApp.controller('IndexUserController', ['$scope', '$timeout', 'Users', 'Organizations',
  function($scope, $timeout, Users, Organizations) {
	$scope.newUser = new Users();
	$scope.newUnit = new Organizations();
	$scope.currentPage = 0;
	$scope.totalPage = 1;
	$scope.units = [];
	$scope.unitsControl = {};
	$scope.inEditMode = false;

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

				$scope.refreshUsers();
				$scope.newUser = new Users();
			},
			function(data, header) {
				$scope.errors = data.data;
				$scope.info = "";
			}
	   );
	};

	$scope.refreshUsers = function(page) {
		Users.query({page: page}, function(data, header) {
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

	$scope.refreshUnits = function() {
		Organizations.query({}, function(data, header) {
			$scope.units = [data];
			$scope.selectedUnit = data;

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
		$scope.selectedUnit = branch;
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
			}
		);
	};

	$scope.refreshUsers(0);
	$scope.refreshUnits();
  }]
);