(function () {
	"use strict";

	function errorBox(form) {
		return form.querySelector("[data-form-error]") || form.parentElement.querySelector("[data-form-error]");
	}

	function showError(form, message) {
		var box = errorBox(form);
		if (box) {
			box.textContent = message;
			box.hidden = false;
		}
	}

	function value(form, name) {
		var field = form.elements[name];
		return field ? field.value.trim() : "";
	}

	function validateAuth(form, type) {
		var email = value(form, "email");
		var password = form.elements.password ? form.elements.password.value : "";
		if (!email || !password) {
			return "Email and password are required.";
		}
		if (type === "register") {
			if (!value(form, "name")) {
				return "Name is required.";
			}
			if (value(form, "name").length > 150) {
				return "Name must be 150 characters or fewer.";
			}
			if (value(form, "department").length > 150) {
				return "Department must be 150 characters or fewer.";
			}
		}
		return "";
	}

	function validateTicket(form) {
		var requiredFields = Array.prototype.slice.call(form.querySelectorAll("[data-required-field]"));
		var invalidField = requiredFields.find(function (field) {
			return !field.value || !field.value.trim();
		});
		if (invalidField) {
			return (invalidField.getAttribute("data-label") || "All required fields") + " must be completed.";
		}
		if (value(form, "subject").length > 200) {
			return "Subject must be 200 characters or fewer.";
		}
		if (value(form, "description").length > 5000) {
			return "Description must be 5,000 characters or fewer.";
		}
		if (value(form, "location").length > 255) {
			return "Location must be 255 characters or fewer.";
		}
		return "";
	}

	function validateComment(form) {
		var comment = value(form, "comment");
		if (!comment) {
			return "Comment cannot be empty.";
		}
		if (comment.length > 2000) {
			return "Comment must be 2,000 characters or fewer.";
		}
		return "";
	}

	function setupValidation() {
		document.querySelectorAll("[data-auth-form]").forEach(function (form) {
			form.addEventListener("submit", function (event) {
				if (event.defaultPrevented) {
					return;
				}
				var message = validateAuth(form, form.getAttribute("data-auth-form"));
				if (message) {
					event.preventDefault();
					showError(form, message);
				}
			});
		});

		document.querySelectorAll("[data-ticket-form]").forEach(function (form) {
			form.addEventListener("submit", function (event) {
				if (event.defaultPrevented) {
					return;
				}
				var message = validateTicket(form);
				if (message) {
					event.preventDefault();
					showError(form, message);
					return;
				}
				disableSubmit(form, "Submitting...");
			});
		});

		document.querySelectorAll("[data-comment-form]").forEach(function (form) {
			form.addEventListener("submit", function (event) {
				if (event.defaultPrevented) {
					return;
				}
				var message = validateComment(form);
				if (message) {
					event.preventDefault();
					showError(form, message);
					return;
				}
				disableSubmit(form, "Adding...");
			});
		});
	}

	function disableSubmit(form, label) {
		var button = form.querySelector("button[type='submit']");
		if (button) {
			button.disabled = true;
			button.textContent = label;
		}
	}

	function setupConfirmations() {
		document.querySelectorAll("[data-confirm]").forEach(function (form) {
			form.addEventListener("submit", function (event) {
				if (!window.confirm(form.getAttribute("data-confirm"))) {
					event.preventDefault();
					var button = form.querySelector("button[type='submit']");
					if (button) {
						button.disabled = false;
					}
				}
			});
		});
	}

	function setupFilterEnhancement() {
		document.querySelectorAll("[data-filter-form]").forEach(function (form) {
			var search = form.querySelector("[data-filter-search]");
			if (search) {
				search.addEventListener("keydown", function (event) {
					if (event.key === "Enter") {
						event.preventDefault();
						form.submit();
					}
				});
			}
		});
	}

	function setupNavigation() {
		var sidebar = document.getElementById("sidebar");
		var navToggle = document.querySelector("[data-nav-toggle]");
		if (sidebar && navToggle) {
			navToggle.addEventListener("click", function () {
				sidebar.classList.toggle("is-open");
			});
		}
	}

	document.addEventListener("DOMContentLoaded", function () {
		setupNavigation();
		setupConfirmations();
		setupValidation();
		setupFilterEnhancement();
	});
}());
