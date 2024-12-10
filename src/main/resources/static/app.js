
// JavaScript to manage conditional "New Date" field requirement
document.getElementById('status').addEventListener('change', function () {
    const newDateField = document.getElementById('newDate');
    if (this.value === 'RESCHEDULED') {
    newDateField.setAttribute('required', 'required');
} else {
    newDateField.removeAttribute('required');
}
});

// Active tab
document.addEventListener("DOMContentLoaded", function () {
    const activeTab = /*[[${activeTab}]]*/ 'manageUsers'; // Default tab if none specified
    const activeTabElement = document.querySelector(`#${activeTab}Tab`);
    const activeContentElement = document.querySelector(`#${activeTab}`);

    if (activeTabElement && activeContentElement) {
        // Activate the tab and content
        activeTabElement.classList.add("active");
        activeContentElement.classList.add("show", "active");
    }
});