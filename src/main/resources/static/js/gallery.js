document.addEventListener('DOMContentLoaded', function () {
    const grid = document.getElementById('gallery-grid');
    const filters = document.querySelectorAll('.filter-checkbox');
    const modal = document.getElementById('imageModal');
    const modalClose = document.querySelector('.modal-close');

    let allPatterns = []; // Store all patterns for modal access
    let validFavoriteIds = new Set(); // Store favorited IDs

    // Initial load
    fetchFavorites().then(() => {
        fetchPatterns();
    });
    initMarquee();

    // Check for avatar input
    const avatarInput = document.getElementById('avatarInput');
    if (avatarInput) {
        avatarInput.addEventListener('change', function (e) {
            const file = e.target.files[0];
            if (!file) return;

            const formData = new FormData();
            formData.append('avatar', file);

            fetch('/api/user/avatar', {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Update image src
                        const img = document.getElementById('currentUserAvatar');
                        if (img) {
                            // Add timestamp to force refresh
                            img.src = data.avatarUrl + '?t=' + new Date().getTime();
                        }
                        alert('头像修改成功！');
                    } else {
                        alert('上传失败: ' + (data.message || '未知错误'));
                    }
                })
                .catch(err => {
                    console.error('Avatar upload error:', err);
                    alert('上传出错');
                });
        });
    }

    // Favorites Filter
    const onlyFavoritesCheckbox = document.getElementById('onlyFavorites');
    if (onlyFavoritesCheckbox) {
        onlyFavoritesCheckbox.addEventListener('change', () => {
            fetchPatterns();
        });
    }

    // Search Input Listener
    const searchInput = document.getElementById('searchInput');
    let searchTimeout;

    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                fetchPatterns();
            }, 300); // 300ms debounce
        });
    }

    // Add event listeners to filters
    filters.forEach(filter => {
        filter.addEventListener('change', () => {
            fetchPatterns();
        });
    });

    // Accordion Logic
    const filterHeaders = document.querySelectorAll('.filter-header');
    filterHeaders.forEach(header => {
        header.addEventListener('click', () => {
            const optionsInfo = header.nextElementSibling;
            const icon = header.querySelector('.filter-icon');

            // Toggle current section
            if (optionsInfo) optionsInfo.classList.toggle('expanded');
            if (icon) icon.classList.toggle('expanded');
        });
    });

    // Collapse all initially except the first one
    // Currently CSS does not collapse by default.

    // Modal close handlers
    if (modalClose) {
        modalClose.addEventListener('click', closeModal);
    }

    if (modal) {
        modal.addEventListener('click', function (e) {
            if (e.target === modal) {
                closeModal();
            }
        });
    }

    // Close modal with ESC key
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            closeModal();
        }
    });

    function fetchFavorites() {
        return fetch('/api/favorites/ids')
            .then(res => res.json())
            .then(ids => {
                validFavoriteIds = new Set(ids);
            })
            .catch(err => console.error('Error fetching favorites:', err));
    }

    function fetchPatterns() {
        const params = new URLSearchParams();

        const formRadio = document.querySelector('input[name="form"]:checked');
        const styleRadio = document.querySelector('input[name="style"]:checked');
        const colorRadio = document.querySelector('input[name="color"]:checked');
        const themeRadio = document.querySelector('input[name="theme"]:checked');

        const form = formRadio ? formRadio.value : '';
        const style = styleRadio ? styleRadio.value : '';
        const color = colorRadio ? colorRadio.value : '';
        const theme = themeRadio ? themeRadio.value : '';
        const keyword = document.getElementById('searchInput') ? document.getElementById('searchInput').value.trim() : '';

        const onlyFavorites = document.getElementById('onlyFavorites') ? document.getElementById('onlyFavorites').checked : false;

        if (form) params.append('form', form);
        if (style) params.append('style', style);
        if (color) params.append('color', color);
        if (theme) params.append('theme', theme);
        if (keyword) params.append('keyword', keyword);
        if (onlyFavorites) params.append('onlyFavorites', 'true');

        fetch(`/api/patterns?${params.toString()}`)
            .then(response => response.json())
            .then(data => {
                allPatterns = data; // Store for modal use
                renderGrid(data);
            })
            .catch(error => console.error('Error fetching patterns:', error));
    }

    function renderGrid(patterns) {
        grid.innerHTML = '';

        // Update result count
        const countDisplay = document.getElementById('resultCount');
        if (countDisplay) {
            countDisplay.textContent = `${patterns.length}`;
            // Add a subtle pop animation
            countDisplay.style.transform = 'scale(1.1)';
            setTimeout(() => countDisplay.style.transform = 'scale(1)', 200);
        }

        if (patterns.length === 0) {
            grid.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--text-secondary);">没有找到匹配的纹样</p>';
            return;
        }

        patterns.forEach((pattern, index) => {
            const card = document.createElement('div');
            card.className = 'pattern-card';
            card.setAttribute('data-pattern-id', pattern.id);

            const imgUrl = pattern.imageUrl || 'https://placehold.co/400x300/1e293b/ffffff?text=Texture';

            // Removed Favorite Button from Card
            card.innerHTML = `
                <img src="${imgUrl}" alt="${pattern.name}" class="pattern-image">
                <div class="pattern-info">
                    <div class="pattern-name">${pattern.name}</div>
                    <div class="pattern-meta">
                        <span class="tag">${pattern.form || '-'}</span>
                        <span class="tag">${pattern.style || '-'}</span>
                    </div>
                </div>
            `;

            // Add click event to open modal
            card.addEventListener('click', () => openModal(pattern));

            grid.appendChild(card);
        });
    }

    window.toggleFavorite = function (event, patternId) {
        event.stopPropagation(); // Prevent modal open

        fetch(`/api/favorite/toggle?patternId=${patternId}`, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    const btn = event.currentTarget;
                    const svg = btn.querySelector('svg');

                    if (data.favorited) {
                        validFavoriteIds.add(patternId);
                        svg.setAttribute('fill', 'currentColor');
                        svg.setAttribute('stroke', '#ef4444');
                        btn.classList.add('active');
                    } else {
                        validFavoriteIds.delete(patternId);
                        svg.setAttribute('fill', 'none');
                        svg.setAttribute('stroke', 'currentColor');
                        btn.classList.remove('active');

                        // If we are in "Only Favorites" mode, remove the card immediately
                        const onlyFavorites = document.getElementById('onlyFavorites') ? document.getElementById('onlyFavorites').checked : false;
                        if (onlyFavorites) {
                            const card = document.querySelector(`.pattern-card[data-pattern-id="${patternId}"]`);
                            if (card) {
                                card.remove();
                                // Update count
                                const countDisplay = document.getElementById('resultCount');
                                if (countDisplay) {
                                    const current = parseInt(countDisplay.textContent.match(/\d+/)[0]) - 1;
                                    countDisplay.textContent = `共 ${current} 张纹样`;
                                }
                            }
                        }
                    }
                } else {
                    if (data.message === '未登录') {
                        alert('请先登录后收藏');
                        window.location.href = '/login';
                    } else {
                        alert('操作失败');
                    }
                }
            })
            .catch(err => console.error(err));
    };

    // Image Viewer Logic
    let currentScale = 1;
    let currentTranslateX = 0;
    let currentTranslateY = 0;
    let isDragging = false;
    let startX = 0;
    let startY = 0;

    const modalImage = document.getElementById('modalImage');
    const zoomLevelDisplay = document.getElementById('zoomLevel');
    const zoomInBtn = document.getElementById('zoomInBtn');
    const zoomOutBtn = document.getElementById('zoomOutBtn');
    const resetZoomBtn = document.getElementById('resetZoomBtn');
    const imageContainer = document.querySelector('.modal-image-container');
    const toolbar = document.querySelector('.image-toolbar');

    // Prevent panning when interacting with toolbar
    if (toolbar) {
        toolbar.addEventListener('mousedown', (e) => e.stopPropagation());
    }

    function updateTransform() {
        modalImage.style.transform = `translate(${currentTranslateX}px, ${currentTranslateY}px) scale(${currentScale})`;
        zoomLevelDisplay.textContent = `${Math.round(currentScale * 100)}%`;
    }

    function setZoom(newScale) {
        // Limit zoom range
        const minScale = 0.5;
        const maxScale = 5.0;

        currentScale = Math.min(Math.max(newScale, minScale), maxScale);

        // If zoomed out to 1 or less, reset translation to keep it centered
        if (currentScale <= 1) {
            currentTranslateX = 0;
            currentTranslateY = 0;
        }

        updateTransform();
    }

    function resetViewer() {
        currentScale = 1;
        currentTranslateX = 0;
        currentTranslateY = 0;
        updateTransform();
    }

    // Event Listeners for Toolbar
    if (zoomInBtn && zoomOutBtn && resetZoomBtn) {
        zoomInBtn.addEventListener('click', (e) => {
            e.stopPropagation(); // Prevent modal close
            setZoom(currentScale + 0.25);
        });

        zoomOutBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            setZoom(currentScale - 0.25);
        });

        resetZoomBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            resetViewer();
        });
    }

    // Mouse Wheel Zoom
    if (imageContainer) {
        imageContainer.addEventListener('wheel', (e) => {
            e.preventDefault();
            const delta = e.deltaY > 0 ? -0.1 : 0.1;
            setZoom(currentScale + delta);
        });

        // Panning Logic
        imageContainer.addEventListener('mousedown', (e) => {
            if (currentScale > 1) {
                isDragging = true;
                startX = e.clientX - currentTranslateX;
                startY = e.clientY - currentTranslateY;
                imageContainer.style.cursor = 'grabbing';
                e.preventDefault(); // Prevent default drag behavior
            }
        });
    }

    window.addEventListener('mousemove', (e) => {
        if (isDragging) {
            e.preventDefault();
            currentTranslateX = e.clientX - startX;
            currentTranslateY = e.clientY - startY;
            updateTransform();
        }
    });

    window.addEventListener('mouseup', () => {
        if (isDragging) {
            isDragging = false;
            if (imageContainer) imageContainer.style.cursor = 'grab';
        }
    });

    function openModal(pattern) {
        // Populate modal with pattern data
        modalImage.src = pattern.imageUrl || 'https://placehold.co/800x600/1e293b/ffffff?text=Texture';
        modalImage.alt = pattern.name;
        document.getElementById('modalTitle').textContent = pattern.name;
        // Populate new fields
        document.getElementById('modalImageCode').textContent = pattern.imageCode || '-';
        document.getElementById('modalResolution').textContent = pattern.resolution || '-';
        document.getElementById('modalImageFormat').textContent = pattern.imageFormat || '-';
        document.getElementById('modalCopyright').textContent = pattern.copyright || '-';
        document.getElementById('modalAuthor').textContent = pattern.author || '-';
        document.getElementById('modalOriginDate').textContent = pattern.originDate || '-';
        document.getElementById('modalRecorder').textContent = pattern.recorder || '-';

        // Category display removed
        document.getElementById('modalDescription').textContent = pattern.description || '暂无描述';
        document.getElementById('modalForm').textContent = pattern.form || '-';
        document.getElementById('modalStyle').textContent = pattern.style || '-';
        document.getElementById('modalColor').textContent = pattern.color || '-';
        document.getElementById('modalTheme').textContent = pattern.theme || '-';

        // Favorite Logic in Modal
        const favBtn = document.getElementById('modalFavoriteBtn');
        const updateFavBtnState = () => {
            const isFav = validFavoriteIds.has(pattern.id);
            const svg = favBtn.querySelector('svg');
            if (isFav) {
                svg.setAttribute('fill', 'currentColor');
                svg.setAttribute('stroke', '#ef4444');
                favBtn.style.color = '#ef4444';
                favBtn.style.background = 'rgba(255, 255, 255, 0.95)'; // White background
                favBtn.style.transform = 'scale(1.1)'; // Slight pulse
            } else {
                svg.setAttribute('fill', 'none');
                svg.setAttribute('stroke', 'currentColor');
                favBtn.style.color = '#7c3aed';
                favBtn.style.background = 'rgba(255, 255, 255, 0.9)'; // White background
                favBtn.style.transform = 'scale(1)';
            }
        };

        updateFavBtnState();

        // Unbind previous listeners (simple clean way: clone and replace)
        const newBtn = favBtn.cloneNode(true);
        favBtn.parentNode.replaceChild(newBtn, favBtn);

        newBtn.addEventListener('click', () => {
            fetch(`/api/favorite/toggle?patternId=${pattern.id}`, { method: 'POST' })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        if (data.favorited) {
                            validFavoriteIds.add(pattern.id);
                        } else {
                            validFavoriteIds.delete(pattern.id);
                        }
                        // Update Modal Button State Immediately
                        const isFav = validFavoriteIds.has(pattern.id);
                        const svg = newBtn.querySelector('svg');
                        if (isFav) {
                            svg.setAttribute('fill', 'currentColor');
                            svg.setAttribute('stroke', '#ef4444');
                            newBtn.style.color = '#ef4444';
                            newBtn.style.background = 'rgba(255, 255, 255, 0.95)';
                            newBtn.style.transform = 'scale(1.1)';
                        } else {
                            svg.setAttribute('fill', 'none');
                            svg.setAttribute('stroke', 'currentColor');
                            newBtn.style.color = '#7c3aed';
                            newBtn.style.background = 'rgba(255, 255, 255, 0.9)';
                            newBtn.style.transform = 'scale(1)';
                        }

                        // NOTE: We do NOT re-render the grid immediately to avoid "jumping" context
                        // while the user is still looking at the modal.
                        // However, if "Only Favorites" is active, and we un-favorite, 
                        // the background grid will still show it until refresh or next fetch.
                        // This is usually desired behavior (don't have things vanish while I'm looking at details).
                        // If users want instant removal from grid, we can trigger fetchPatterns() on closeModal.
                    } else {
                        if (data.message === '未登录') {
                            alert('请先登录后收藏');
                            window.location.href = '/login';
                        } else {
                            alert('操作失败');
                        }
                    }
                })
                .catch(err => console.error(err));
        });

        // Reset viewer state
        resetViewer();

        // Show modal
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden'; // Prevent background scrolling
    }

    function closeModal() {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto'; // Restore scrolling
        resetViewer();

        // If "Only Favorites" is checked, we should refresh the grid to reflect removals
        const onlyFavorites = document.getElementById('onlyFavorites') ? document.getElementById('onlyFavorites').checked : false;
        if (onlyFavorites) {
            fetchPatterns();
        }
    }

    function initMarquee() {
        const marqueeContent = document.getElementById('marqueeContent');
        if (!marqueeContent) return;

        // Fetch "some" patterns, maybe all or a random subset
        fetch('/api/patterns')
            .then(response => response.json())
            .then(patterns => {
                if (patterns.length === 0) return;

                // Shuffle patterns for randomness
                const shuffled = patterns.sort(() => 0.5 - Math.random());
                const selected = shuffled.slice(0, 15); // Take up to 15 images

                const createItem = (pattern) => {
                    const img = document.createElement('img');
                    img.src = pattern.imageUrl;
                    img.alt = '';
                    img.className = 'marquee-item';
                    img.onerror = () => { img.style.display = 'none'; }; // Hide broken images
                    return img;
                };

                // Add original items
                selected.forEach(p => marqueeContent.appendChild(createItem(p)));

                // Content must be duplicated to ensure smooth infinite scroll
                // We need enough duplicates to fill screen + overflow
                // Simple approach: duplicate the whole set once or twice
                selected.forEach(p => marqueeContent.appendChild(createItem(p)));
                selected.forEach(p => marqueeContent.appendChild(createItem(p)));
            })
            .catch(e => console.error('Marquee fetch error:', e));
    }
});
