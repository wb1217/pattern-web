document.addEventListener('DOMContentLoaded', function () {
    const grid = document.getElementById('gallery-grid');
    const filters = document.querySelectorAll('.filter-checkbox');

    // Initial load
    fetchPatterns();

    // Add event listeners to filters
    filters.forEach(filter => {
        filter.addEventListener('change', () => {
            fetchPatterns();
        });
    });

    function fetchPatterns() {
        const params = new URLSearchParams();

        const form = document.querySelector('input[name="form"]:checked').value;
        const style = document.querySelector('input[name="style"]:checked').value;
        const color = document.querySelector('input[name="color"]:checked').value;
        const theme = document.querySelector('input[name="theme"]:checked').value;

        if (form) params.append('form', form);
        if (style) params.append('style', style);
        if (color) params.append('color', color);
        if (theme) params.append('theme', theme);

        fetch(`/api/patterns?${params.toString()}`)
            .then(response => response.json())
            .then(data => {
                renderGrid(data);
            })
            .catch(error => console.error('Error fetching patterns:', error));
    }

    function renderGrid(patterns) {
        grid.innerHTML = '';

        if (patterns.length === 0) {
            grid.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--text-secondary);">No patterns found matching your criteria.</p>';
            return;
        }

        patterns.forEach(pattern => {
            const card = document.createElement('div');
            card.className = 'pattern-card';

            // Use placeholder if no image url or if it fails (handled by onerror in real app, here simple check)
            const imgUrl = pattern.imageUrl || 'https://placehold.co/400x300/1e293b/ffffff?text=Texture';

            card.innerHTML = `
                <img src="${imgUrl}" alt="${pattern.name}" class="pattern-image">
                <div class="pattern-info">
                    <div class="pattern-name">${pattern.name}</div>
                    <div class="pattern-meta">
                        <span class="tag">${pattern.form || '-'}</span>
                        <span class="tag">${pattern.style || '-'}</span>
                        <span class="tag">${pattern.color || '-'}</span>
                    </div>
                </div>
            `;
            grid.appendChild(card);
        });
    }
});
