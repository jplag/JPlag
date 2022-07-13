
 const radarChartStyle ={
    fill: true,
    backgroundColor: "rgba(149, 168, 241, 0.5)",
    borderColor: "rgba(149, 168, 241, 1)",
    pointBackgroundColor: "rgba(149, 168, 241, 1)",
    pointBorderColor: "#fff",
    pointHoverBackgroundColor: "#fff",
    pointHoverBorderColor: "rgb(255, 99, 132)",
  }
  const radarChartOptions = {
    legend: {
      display: false,
    },
    scales: {
      r: {
        suggestedMin: 50,
        suggestedMax: 100,
      },
    },
  }
  export {radarChartStyle,radarChartOptions}