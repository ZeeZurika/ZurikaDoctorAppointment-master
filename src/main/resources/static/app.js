// Dynamically show/hide the "New Date" field based on selected status
document.getElementById('status').addEventListener('change', function () {
    const rescheduleDate = document.getElementById('rescheduleDate');
    if (this.value === 'RESCHEDULED') {
        rescheduleDate.style.display = 'block';
        document.getElementById('newDate').setAttribute('required', 'required');
    } else {
        rescheduleDate.style.display = 'none';
        document.getElementById('newDate').removeAttribute('required');
    }
});

// Automatically activate the tab based on the URL hash
document.addEventListener('DOMContentLoaded', function () {
    const hash = window.location.hash; // Get the hash (e.g., #manageAppointments)

    // Check if the hash matches a tab's href
    if (hash) {
        const tabLink = document.querySelector(`a[href="${hash}"]`);
        const tabContent = document.querySelector(hash);

        if (tabLink && tabContent) {
            // Deactivate all tabs and contents
            document.querySelectorAll('.nav-link').forEach(tab => tab.classList.remove('active'));
            document.querySelectorAll('.tab-pane').forEach(content => content.classList.remove('show', 'active'));

            // Activate the selected tab and content
            tabLink.classList.add('active');
            tabContent.classList.add('show', 'active');
        }
    }
});
