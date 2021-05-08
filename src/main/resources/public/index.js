document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('search').addEventListener('click', () => {
        asyncSvn();
    })

    document.getElementById('pagingRender').addEventListener('click', (event) => {
        let el = event.target;
        let page = el.getAttribute('arial-page');
        if(page) {
            asyncSvn(page);
        }
    });

    window.addEventListener('popstate', (event) => {
        let state = event.state;
        if(state.data) {
            renderData(state.data);
        }
    });

    // Initialize all input of type date
    var calendars = bulmaCalendar.attach('[type="date"]', {
        type:'date',
        isRange: true,
        dateFormat: 'YYYY-MM-DD',
        startDate: new Date(new Date().setDate(new Date().getDate() - 3)),
        endDate: new Date(),
        onReady: function(datepicker) {
            let value = datepicker.data.value();
            let d = value.split(' - ');
            document.getElementById('startDate').value = d[0];
            document.getElementById('endDate').value = d[1];
        }
    });

    // To access to bulmaCalendar instance of an element
    var element = document.querySelector('#datepicker');
    if (element) {
        // bulmaCalendar instance is available as element.bulmaCalendar
        element.bulmaCalendar.on('select', (datepicker) => {
            let value = datepicker.data.value();
            let d = value.split(' - ');
            document.getElementById('startDate').value = d[0];
            document.getElementById('endDate').value = d[1];
        });
    }
});

// async
async function asyncSvn(page) {
    let res = await fetchSvnData( page ? `/svn/${page}` :'/svn');
    let json = await res.json();

    await renderData(json);
    history.pushState({ data: json }, document.title, page ? `/svn/${page}` :'/svn');
}

async function renderData(json) {
    async function fetchToText(url) {
        const res = await fetch(url)
        return res.text();
    }

    let tableTemplate = await fetchToText('/template/table.mustache');
    let tableRendered = Mustache.render(tableTemplate, json);
    document.getElementById('tableRender').innerHTML = tableRendered;

    let pagingTemplate = await fetchToText('/template/paging.mustache');
    let pagingRendered = Mustache.render(pagingTemplate, json);
    document.getElementById('pagingRender').innerHTML = pagingRendered;
}

async function fetchSvnData(url) {
    let inputList = document.querySelectorAll('input')
    let params = "";
    for (let input of inputList) {
        let name = input.getAttribute('name');
        if (name) {
            params += `${name}=${input.value}&`;
        }
    }

    if(params !== "") {
        url += `?${params.slice(0, params.length - 1)}`;
    }

    return fetch(url, {
        headers: {
            'Accept': 'application/json'
        }
    });
}

// promise
function promiseSvn(page) {
    fetchSvnData(page ? `/svn/${page}` :'/svn')
        .then(res => res.json())
        .then(json => {
            fetch('/template/table.mustache')
                .then(res => res.text())
                .then(tableTemplate => {
                    let rendered = Mustache.render(tableTemplate, json);
                    document.getElementById('tableRender').innerHTML = rendered;
                });

            fetch('/template/paging.mustache')
                .then(res => res.text())
                .then(pagingTemplate => {
                    let rendered = Mustache.render(pagingTemplate, json);
                    document.getElementById('pagingRender').innerHTML = rendered;
                });
        });
}