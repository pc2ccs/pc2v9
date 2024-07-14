const getCellValue = (tr, idx) => tr.children[idx].innerText || tr.children[idx].textContent;

const comparer = (idx, asc) => (a, b) => ((v1, v2) =>
    v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2, undefined, {numeric: true})
    )(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));

// do the work...
document.querySelectorAll('th').forEach(th => th.addEventListener('click', (() => {
    const table = th.closest('table');
    if (th.querySelector('span')){
        let order_icon = th.querySelector('span');
        // Awesome? hack: use the icon as a sort indicator for the column.  we convert
        // it to a URI and look at the UTF-8 encoding. Calm yourself.  I found this code
        // here: https://github.com/VFDouglas/HTML-Order-Table-By-Column/blob/main/index.html
        // and hacked it up further. -- JB
        let order = encodeURI(order_icon.innerHTML).includes('%E2%96%B2') ? 'desc' : 'asc';
        Array.from(table.querySelectorAll('tr:nth-child(n+2)'))
            .sort(comparer(Array.from(th.parentNode.children).indexOf(th), order === 'asc'))
            .forEach(tr => table.appendChild(tr) );
        if(order === 'desc') {
            // down triangle
            order_icon.innerHTML = "&#x25bc;"
        } else {
            // up triangle
            order_icon.innerHTML = "&#x25b2;"
        }
    }
})));
