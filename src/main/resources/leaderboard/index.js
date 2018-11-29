
const START_URL = '/start';
const PAUSE_URL = '/pause';
const GAME_STATE_URL = '/game';
const UPDATE_RATE = 2000; // 2 sec

updateLeaderboard();

function updateLeaderboard() {
    fetch(GAME_STATE_URL)
        .then(data => { return data.json() })
        .then(res => {
            //console.log(res);
            updateGameState(res.running);
            updateTable(res.players);
            updateChart(res.players);
        });
}

function updateGameState(isGameRunning) {
    document.getElementById('state').style.backgroundColor = isGameRunning ? 'green' : 'red'
}

function start() {
    fetch(START_URL, {method: "POST"});
}

function pause() {
    fetch(PAUSE_URL, {method: "POST"});
}

/* Leaderboard HTML Table */

function updateTable(players) {
    var tbody = document.createElement('tbody');
    players.forEach(player => {
        addRow(tbody, player);
    })
    var oldTbody = document.getElementsByTagName("tbody").item(0);
    oldTbody.parentNode.replaceChild(tbody, oldTbody)
}

function addRow(tbody, player) {
    var row = document.createElement("tr");
    var cell1 = document.createElement("td");
    var cell2 = document.createElement("td");
    var cell3 = document.createElement("td");
    var textnode1 = document.createTextNode(player.name);
    var textnode2 = document.createTextNode(player.score);
    var textnode3 = document.createTextNode(player.maxChallengeAchieved);
    cell1.appendChild(textnode1);
    cell2.appendChild(textnode2);
    cell3.appendChild(textnode3);
    row.appendChild(cell1);
    row.appendChild(cell2);
    row.appendChild(cell3);
    if (player.finishedAllChallenges) {
        var cell4 = document.createElement("td");
        var textnode4 = document.createTextNode("👑");
        cell4.appendChild(textnode4);
        row.appendChild(cell4);
    }
    tbody.appendChild(row);
    return tbody;
}


/* ChartJs Progress graph */

var playersSet = new Set();
var color = Chart.helpers.color;
var timeFormat = 'MM/DD/YYYY HH:mm';
var chart;


function updateChart(players) {
    if (players.length > playersSet.size) {
        players.forEach(player => {
            if (!playersSet.has(player.name)) {
                playersSet.add(player.name);
                createChart(players);
            }
        })
    } else if (chart !== undefined) {
        updateChartWithPlayerData(players);
    }
    setTimeout(updateLeaderboard, UPDATE_RATE);
}

function createChart(players) {
    var config = createChartconfig();
    var colors = ['rgb(54, 162, 235)', 'rgb(75, 192, 192)', 'rgb(255, 99, 132)', 'rgb(255, 159, 64)', 'rgb(153, 102, 255)', 'rgb(201, 203, 207)', 'rgb(255, 205, 86)'];
    players.forEach(player => {
        var col = colors[Math.floor(Math.random() * colors.length)];
        colors.splice(colors.indexOf(col), 1);
        config.data.datasets.push({
            label: player.name,
            backgroundColor: color(col).alpha(0.5).rgbString(),
            borderColor: col,
            fill: false,
            data: []
        });
    });
    var ctx = document.getElementById("chart").getContext('2d');
    chart = new Chart(ctx, config);
}

function updateChartWithPlayerData(players) {
    var date = new Date();
    var needUpdate = false;
    players.forEach(player => {
        chart.data.datasets.forEach(dataset => {
            if (dataset.label === player.name &&
                (dataset.data.length == 0 ||
                (dataset.data.length > 0 && dataset.data[dataset.data.length - 1].y !== player.score))) {
                dataset.data.push({x: date, y: player.score});
                needUpdate = true;
            }
        });
    });
    if (needUpdate) {
        chart.update();
    }
}

function createChartconfig() {
    return {
        type: 'line',
        data: {
            datasets: []
        },
        options: {
            scales: {
                xAxes: [{
                    type: 'time',
                    time: {
                        format: timeFormat,
                    },
                    scaleLabel: {
                        display: true,
                        labelString: 'Date'
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'score'
                    }
                }]
            },
        }
    };
}
