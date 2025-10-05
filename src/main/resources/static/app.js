const listEl = document.getElementById('list');
const refreshBtn = document.getElementById('refresh');

async function load() {
	listEl.innerHTML = '<tr><td colspan="5">Loading...</td></tr>';
	try {
		const res = await fetch('/articles');
		if (!res.ok) throw new Error('Fetch failed');
		const data = await res.json();
		if (!Array.isArray(data) || data.length === 0) {
			listEl.innerHTML = '<tr><td colspan="5">No articles</td></tr>';
			return;
		}
		listEl.innerHTML = data.map(a => `
			<tr>
				<td>${a.id || ''}</td>
				<td>${a.paperTitle || ''}</td>
				<td>${a.authors || ''}</td>
				<td>${a.publicationYear || ''}</td>
				<td><button class="btn delete" data-id="${a.id}">Delete</button></td>
			</tr>
		`).join('');

		document.querySelectorAll('.btn.delete').forEach(btn => {
			btn.addEventListener('click', async (e) => {
				const id = e.target.dataset.id;
				if (!confirm('Delete article ' + id + '?')) return;
				const resp = await fetch('/articles/' + id, { method: 'DELETE' });
				if (resp.ok) load(); else alert('Delete failed');
			});
		});
	} catch (e) {
		listEl.innerHTML = '<tr><td colspan="5">Error loading: ' + e.message + '</td></tr>';
	}
}

refreshBtn.addEventListener('click', load);
load();