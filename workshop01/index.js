(function() {
	var NotesApp = angular.module("NotesApp", []);

	var NotesCtrl = function() {
		var notesCtrl = this;

		notesCtrl.notes = [];

		notesCtrl.aNote = {
			title: "",
			content: ""
		};

		notesCtrl.add = function() {
			notesCtrl.notes.push(notesCtrl.aNote);
			notesCtrl.aNote = {
				title: "",
				content: ""
			};
		};

		notesCtrl.remove = function($index) {
			notesCtrl.notes.splice($index, 1);
		};
	};

	NotesApp.controller("NotesCtrl", [NotesCtrl]);
})();
