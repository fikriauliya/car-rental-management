reservationManagementApp = angular.module('reservationManagementApp');
reservationManagementApp.controller('ConfirmationController', ['$scope', '$timeout', '$location', 'Customers', 'Images', 'TimezoneConverter', 'ngProgress', '$cookieStore', 'Inventories', 'Reservations',
  function($scope, $timeout, $location, Customers, Images, TimezoneConverter, ngProgress, $cookieStore, Inventories, Reservations) {
	console.log($location.search());
	$scope.id = ($location.search()).id;
}]);