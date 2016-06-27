
//get the walk object from the URL string
var walkName =  getParameterByName("walk");
//console.log("--" + walkName + "--");
//set up firebase references
var walkInfoRef = new Firebase('https://wheeledwalks.firebaseio.com/Walk_Info/Walks');
var walkRef = walkInfoRef.child(walkName);
var logRef = new Firebase('https://wheeledwalks.firebaseio.com/Walk_Logs/Walks/'+ walkName);
var devicesRef = new Firebase('https://wheeledwalks.firebaseio.com/Processed/Walks/'+ walkName + '/Info/Devices');
var trackRef = new Firebase('https://wheeledwalks.firebaseio.com/Processed/Walks/'+ walkName + '/Track');


var RawDevicesArray = [];  //read directly from the database
var FilteredDevicesArray = []; //data is filtered
var devicesArray = []; //used for graphing
var filtersArray = []; //used for storing the active filters
var trackArray = []; //used for storing the gps track and gradient data
var walk;
var graphDiv = document.getElementById("graphContainer"); //get the graph container
var gradientDivSize = 0;

var gradientColours = ["#C4EDEC","#BBEEDD","#B2EFC9","#A8F0AE","#B2F29F","#C7F396","#E2F48C","#F6E783","#F7C179","#F9656A"];

showLoader(true);

// get and display the connected devices and options and stores device list in RawDevicesArray[]
devicesRef.on('child_added', function(snapshot) {

    var device = snapshot.val();
    RawDevicesArray.push(device);
    console.log("adding device: " + device.alias);

});



//get the walk info from the references and store it as walk
walkRef.once("value", function(snapshot) {
        walk = snapshot.val();
        //  console.log(walk.name + walk.locality);
    }, function (errorObject) {
        console.log("The read failed: " + errorObject.code);
});

trackRef.on("child_added", function(snapshot) {
         var segment = snapshot.val();
         trackArray.push(segment);
    }, function (errorObject) {
        console.log("The read failed: " + errorObject.code);
});


//function to get a parameter from the URL by name (if URL input is empty, uses current window URL)
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
    results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

//Function to create data arrays for input into Dygraphs
logRef.once("value", function(snapshot) {
    var deviceIndex, numDevices;
    //for each device in the raw data
    for (deviceIndex = 0, numDevices = RawDevicesArray.length; deviceIndex < numDevices; ++deviceIndex) {
        device = RawDevicesArray[deviceIndex];
        console.log("Reading Data from Device: " + device.name + " at address: " + device.address);
        deviceDataRef = snapshot.child(device.address);

        //write each set of sensor data to its own array, then push to the device data array

        //write timestamps as dates
        device.data = [];
        var tempArray = [];
        deviceDataRef.child("Timestamps").forEach(function(child){
            tempArray.push(new Date(child.val()));
        });
        device.data.push(tempArray);

        //write the rest of the values into the device data array
        if (device.name == "Polar H7 3958881C"){ // only push heartrate
            var tempArray = [];
            deviceDataRef.child("HeartRate").forEach(function(child){
                tempArray.push(child.val());
            });
            device.data.push(tempArray);

        } else if (device.name == "CC2650 SensorTag"){

            var tempArray = [];
            deviceDataRef.child("Accelerometer").child("X").forEach(function(child){
                tempArray.push(child.val());
            });
            device.data.push(tempArray);

            tempArray = [];
            deviceDataRef.child("Accelerometer").child("Y").forEach(function(child){
                tempArray.push(child.val());
            });
            device.data.push(tempArray);

            tempArray = [];
            deviceDataRef.child("Accelerometer").child("Z").forEach(function(child){
                tempArray.push(child.val());
            });
            device.data.push(tempArray);

            var tempArray = [];
            deviceDataRef.child("Compass").child("X").forEach(function(child){
                tempArray.push(child.val());
            });
            device.data.push(tempArray);

            tempArray = [];
            deviceDataRef.child("Compass").child("Y").forEach(function(child){
            tempArray.push(child.val());
            });
            device.data.push(tempArray);

            tempArray = [];
            deviceDataRef.child("Compass").child("Z").forEach(function(child){
            tempArray.push(child.val());
            });
            device.data.push(tempArray);

            var tempArray = [];
            deviceDataRef.child("Gyroscope").child("X").forEach(function(child){
            tempArray.push(child.val());
            });
            device.data.push(tempArray);

            tempArray = [];
            deviceDataRef.child("Gyroscope").child("Y").forEach(function(child){
            tempArray.push(child.val());
            });
            device.data.push(tempArray);

            tempArray = [];
            deviceDataRef.child("Gyroscope").child("Z").forEach(function(child){
            tempArray.push(child.val());
            });
            device.data.push(tempArray);
        }


    }
    //display the devices in the menu
    displaySensorTagOptions();
    updateGraph(false);
});



function showLoader(show){
    var loader = document.getElementById("loading"); //get the graph container
    if(show){
        loader.style.visibility = 'visible';
    } else{
        loader.style.visibility = 'hidden';
        graphDiv.style.visibility = 'visible';
    }
}

//recreates the graphing data using either filtered or raw data, and writes it to the graph
function updateGraph(filtered) {

    getEnabledSensors();
    if(!filtered){
        console.log("graphing raw data...");
        //devicesArray = jQuery.extend(true,{}, RawDevicesArray);
        devicesArray = cloneDevicesArray(RawDevicesArray);
    } else {
        console.log("graphing filtered data...");
        devicesArray = cloneDevicesArray(FilteredDevicesArray);
    }
    showLoader(true); //show loading spinner

    var graphArray = [];
    var sensorCounters = [];
    var sensorIndexes = [];
    var columnIndex, numColumns;
    var labelsArray = getLabels();
    var allSensorsFinished = false;
    var nextSensor = 0;

    //create sensor counters
    for (deviceIndex = 0;  deviceIndex <devicesArray.length; deviceIndex++) {
    console.log("device index: " + deviceIndex + " length = " + devicesArray[deviceIndex].data[0].length);
        sensorCounters.push(devicesArray[deviceIndex].data[0].length);
        sensorIndexes.push(0); //add an index for the sensor
    }
    console.log("sensor counters created: ");
    console.log(sensorCounters);
    while (!allSensorsFinished){
        //find next sensor
        var nextDate = new Date();
        for (deviceIndex = 0;  deviceIndex < devicesArray.length; deviceIndex++) {
                if(!sensorCounters[deviceIndex] == 0){ //still data in sensor
                    if (devicesArray[deviceIndex].data[0][sensorIndexes[deviceIndex]] < nextDate){ //if this sensor is older
                        nextDate = devicesArray[deviceIndex].data[0][sensorIndexes[deviceIndex]];
                        nextSensor = deviceIndex;
                        //console.log("Next Device: " + nextSensor);
                    }
                }
            } //nextSensor is the index of the oldest sensor (probably)
        //create a row
        var row = [];
        //add timestamp from selected sensor
        row.push(devicesArray[nextSensor].data[0][sensorIndexes[nextSensor]]);
        //fill array row with nulls and data
        for (deviceIndex = 0;  deviceIndex < devicesArray.length; deviceIndex++) {
            //loop through all the devices
            for (sensorIndex = 1;  sensorIndex < devicesArray[deviceIndex].data.length; sensorIndex++) {

                if(devicesArray[deviceIndex].enabledSensors[sensorIndex-1]){ //get the sensor from the device (timestamp not included, hence the -1)
                    if (deviceIndex == nextSensor){ //if this device is the current one
                        row.push(devicesArray[deviceIndex].data[sensorIndex][sensorIndexes[deviceIndex]]);
                    }
                    else{ //write null values for this device
                        row.push(null);
                    }
                }
            }
        }
        //add row to graph
        graphArray.push(row);
        //decrement sensor counter and increase index
        sensorCounters[nextSensor]--;
        sensorIndexes[nextSensor]++;
        //console.log("sensor counters: " + sensorCounters);
        //check if finished
        allSensorsFinished = isFinished(sensorCounters);
    }
    console.log("Loop exited. Graphing result...");
    //console.log(graphArray);


    //graph it :)
    var graph = new Dygraph(graphDiv, graphArray, {
        labels: labelsArray.slice(),
        connectSeparatedPoints: true,
        //highlight settings
        highlightCircleSize: 2,
        highlightSeriesOpts: {
              strokeWidth: 2,
              strokeBorderWidth: 1,
              highlightCircleSize: 5
        },
        //gradient settings
        underlayCallback: function(canvas, area, g) {
            if (gradientDivSize == 0){
                //no gradient is applied
                return;
            } else {
                var zeroCoords = g.toDomCoords(0, 0);
                var zeroX = zeroCoords[0];
                var zeroY = zeroCoords[1];
                var divCoords = g.toDomCoords(0, gradientDivSize);
                var divHeight = divCoords[1] - zeroY;
                console.log ("div height: " + divHeight);
                // fillRect(x, y, width, height)
                canvas.fillStyle = gradientColours[9];
                canvas.fillRect(area.x,area.y, area.w, area.h);

                for (var i = 0; i < 10; i++){
                    canvas.fillStyle = gradientColours[i];
                    canvas.fillRect(area.x,zeroY + (divHeight * i), area.w, divHeight);
                }
            }
        }
    });
    //set up highlighting on clicking a dataset
    var onclick = function(ev) {
        if (graph.isSeriesLocked()) {
          graph.clearSelection();
        } else {
          graph.setSelection(graph.getSelection(), graph.getHighlightSeries(), true);
        }
    };
    graph.updateOptions({clickCallback: onclick}, true);
    showLoader(false);
}

//check if the counter array contains all zeros
function isFinished(array) {
    for(var i = 0; i < array.length; i++) {
        if(array[i] != 0) {
            return false;
        }
    }
    //console.log("Array contains all zeros!")
    return true;
}


//get the labels of all the enabled sensors and return them as an array
function getLabels(){
    var labelsArray = [];
    for (deviceIndex = 0;  deviceIndex < RawDevicesArray.length; ++deviceIndex) {


        if (deviceIndex == 0){ //only want timestamp label once
            labelsArray.push("time");
        }

        if (RawDevicesArray[deviceIndex].name == "CC2650 SensorTag"){
            if (RawDevicesArray[deviceIndex].enabledSensors[0]){
                labelsArray.push(RawDevicesArray[deviceIndex].alias + "_AccelX");
            }
            if (RawDevicesArray[deviceIndex].enabledSensors[1]){
                labelsArray.push(RawDevicesArray[deviceIndex].alias + "_AccelY");
            }
            if (RawDevicesArray[deviceIndex].enabledSensors[2]){
                labelsArray.push(RawDevicesArray[deviceIndex].alias + "_AccelZ");
            }
            if (RawDevicesArray[deviceIndex].enabledSensors[3]){
                labelsArray.push(RawDevicesArray[deviceIndex].alias + "_CompassX");
            }
            if (RawDevicesArray[deviceIndex].enabledSensors[4]){
                labelsArray.push(RawDevicesArray[deviceIndex].alias + "_CompassY");
            }
            if (RawDevicesArray[deviceIndex].enabledSensors[5]){
                labelsArray.push(RawDevicesArray[deviceIndex].alias + "_CompassZ");
            }
            if (RawDevicesArray[deviceIndex].enabledSensors[6]){
                labelsArray.push(RawDevicesArray[deviceIndex].alias + "_GyroX");
            }
            if (RawDevicesArray[deviceIndex].enabledSensors[7]){
                labelsArray.push(RawDevicesArray[deviceIndex].alias + "_GyroY");
            }
            if (RawDevicesArray[deviceIndex].enabledSensors[8]){
                labelsArray.push(RawDevicesArray[deviceIndex].alias + "_GyroZ");
            }
        } else if (RawDevicesArray[deviceIndex].name == "Polar H7 3958881C"){
            if (RawDevicesArray[deviceIndex].enabledSensors[0]){
                labelsArray.push("HeartRate");
            }
        }

    }
    console.log("Labels: " + labelsArray);
return labelsArray;
}

//check the user input for selected sensors and write them as an array to each device object
function getEnabledSensors(){
    for (deviceIndex = 0;  deviceIndex < RawDevicesArray.length; ++deviceIndex) {
        var tempEnabledSensors = [];
        if (RawDevicesArray[deviceIndex].name == "CC2650 SensorTag"){
            tempEnabledSensors.push(document.getElementById("AccelerometerX"+deviceIndex).checked)
            tempEnabledSensors.push(document.getElementById("AccelerometerY"+deviceIndex).checked)
            tempEnabledSensors.push(document.getElementById("AccelerometerZ"+deviceIndex).checked)
            tempEnabledSensors.push(document.getElementById("CompassX"+deviceIndex).checked)
            tempEnabledSensors.push(document.getElementById("CompassY"+deviceIndex).checked)
            tempEnabledSensors.push(document.getElementById("CompassZ"+deviceIndex).checked)
            tempEnabledSensors.push(document.getElementById("GyroscopeX"+deviceIndex).checked)
            tempEnabledSensors.push(document.getElementById("GyroscopeY"+deviceIndex).checked)
            tempEnabledSensors.push(document.getElementById("GyroscopeZ"+deviceIndex).checked)

        } else if (RawDevicesArray[deviceIndex].name == "Polar H7 3958881C"){
            tempEnabledSensors.push(document.getElementById("Heart Rate"+deviceIndex).checked)
        }
        RawDevicesArray[deviceIndex].enabledSensors = tempEnabledSensors;
    }


}

function displaySensorTagOptions() {

    var devicesDiv = document.getElementById("devicesDiv"); //get the root element to add devices to

    for (deviceIndex = 0;  deviceIndex < RawDevicesArray.length; ++deviceIndex) {
        var cardDiv = document.createElement("div"); //create the card background
              cardDiv.classList.add("deviceCardBackground"); //set the style


        var devTitle = document.createElement("h5");
              devTitle.appendChild(document.createTextNode(RawDevicesArray[deviceIndex].alias));
              devTitle.align = "center";
              cardDiv.appendChild(devTitle);
              if (RawDevicesArray[deviceIndex].name == "CC2650 SensorTag"){
                    createSensorDiv("Accelerometer");
                    cardDiv.appendChild(createSensorDiv("Accelerometer", deviceIndex));
                    cardDiv.appendChild(createSensorDiv("Compass", deviceIndex));
                    cardDiv.appendChild(createSensorDiv("Gyroscope", deviceIndex));
              } else if (RawDevicesArray[deviceIndex].name == "Polar H7 3958881C"){
                    cardDiv.appendChild(createSensorDiv("Heart Rate", deviceIndex));
              }


        devicesDiv.appendChild(cardDiv);
    }
}

function createSensorDiv(sensorType, sensorNumber){
    var newDiv = document.createElement("div"); //create the card background
    newDiv.classList.add("sensorDiv"); //set the style

    var sensorTitle = document.createElement("p");
    sensorTitle.appendChild(document.createTextNode(sensorType));
    sensorTitle.classList.add("sensorTypeText"); //set the style
    newDiv.appendChild(sensorTitle);

    var table = document.createElement("table"); //create the checkbox table
        table.classList.add("sensorTable"); //set the style


        if (sensorType == "Heart Rate"){
            var row = document.createElement("tr"); //create the checkbox row
                        var tableData = document.createElement("td"); //create the checkbox label
                        tableData.appendChild(document.createTextNode("Sensor Enabled"));
                        row.appendChild(tableData);
                        table.appendChild(row);
                        var row = document.createElement("tr"); //create the checkbox row
                        var tableData = document.createElement("td"); //create the checkbox label
                        var newCheckbox = createNewCheckbox("name", sensorType + sensorNumber);

                        newCheckbox.checked = true; //set heartrate to true to start off

                        tableData.appendChild(newCheckbox);
                        row.appendChild(tableData);
                        table.appendChild(row);
        } else {
            var row = document.createElement("tr"); //create the checkbox row
            var tableData = document.createElement("td"); //create the checkbox label
            tableData.appendChild(document.createTextNode("X"));
            row.appendChild(tableData);
            var tableData = document.createElement("td"); //create the checkbox label
            tableData.appendChild(document.createTextNode("Y"));
            row.appendChild(tableData);
            var tableData = document.createElement("td"); //create the checkbox label
            tableData.appendChild(document.createTextNode("Z"));
            row.appendChild(tableData);
            table.appendChild(row);

            var row = document.createElement("tr"); //create the checkbox row
            var tableData = document.createElement("td"); //create the checkbox label
            var newCheckbox = createNewCheckbox("name", sensorType + "X" + sensorNumber);
            if (sensorType == "Accelerometer"){
                newCheckbox.checked = true; //set all accel x to true to start off
                 }
            tableData.appendChild(newCheckbox);
            row.appendChild(tableData);
            var tableData = document.createElement("td"); //create the checkbox label
            tableData.appendChild(createNewCheckbox("name", sensorType + "Y" + sensorNumber));
            row.appendChild(tableData);
            var tableData = document.createElement("td"); //create the checkbox label
            tableData.appendChild(createNewCheckbox("name", sensorType + "Z" + sensorNumber));
            row.appendChild(tableData);
            table.appendChild(row);
            }

    newDiv.appendChild(table);
    return newDiv;
}

function createNewCheckbox(name, id){
    var checkbox = document.createElement('input');
    checkbox.type= 'checkbox';
    checkbox.name = name;
    checkbox.id = id;
    return checkbox;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                   FILTERING SCRIPTS                                       ///////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

var filterDiv = document.getElementById("FilterContainerDiv"); //get the root element to add filters
var filterTypeComboBox = document.getElementById("filterTypes");

//add a filter card to the div

function addFilter(){
    var newFilterDiv = document.createElement("div"); //create the card background
    newFilterDiv.classList.add("filterDiv"); //set the style
    var filterType = filterTypeComboBox.options[filterTypeComboBox.selectedIndex].text;
    var filterTypeHeader = document.createElement("h5");
    filterTypeHeader.appendChild(document.createTextNode(filterType));
    filterTypeHeader.classList.add("FilterTypeText"); //set the style
    newFilterDiv.appendChild(filterTypeHeader);

    var filter = {type: filterType};
    filtersArray.push(filter);

    switch (filterType){
        case "High Pass":
        case "Low Pass":
            var cutoffLabel = document.createElement("p");
            cutoffLabel.appendChild(document.createTextNode("Cutoff: "));
            cutoffLabel.id = "cutoffLabel" + (filtersArray.length - 1); //index of filter in array
            newFilterDiv.appendChild(cutoffLabel);

            var cutoffSlider = document.createElement("input");
            cutoffSlider.id = "cutoff" + (filtersArray.length - 1); //index of filter in array
            cutoffSlider.type = "range";
            cutoffSlider.min = 1;
            cutoffSlider.max = 100;
            cutoffSlider.value = 1;

            newFilterDiv.appendChild(cutoffSlider);

            var smoothingLabel = document.createElement("p");
            smoothingLabel.appendChild(document.createTextNode("Smoothing: "));
            smoothingLabel.id = "smoothingLabel" + (filtersArray.length - 1); //index of filter in array
            newFilterDiv.appendChild(smoothingLabel);

            var smoothingSlider = document.createElement("input");
            smoothingSlider.id = "smoothing" + (filtersArray.length - 1); //index of filter in array
            smoothingSlider.type = "range";
            smoothingSlider.min = 1;
            smoothingSlider.max = 10;
            smoothingSlider.value = 1;

            newFilterDiv.appendChild(smoothingSlider);
            filterDiv.appendChild(newFilterDiv);
            cutoffSlider.addEventListener("input", showValue);
            cutoffSlider.addEventListener("change", testFilters);
            smoothingSlider.addEventListener("input", showValue);
            smoothingSlider.addEventListener("change", testFilters);
            break;
        case "Normalize":
            var levelLabel = document.createElement("p");
            levelLabel.appendChild(document.createTextNode("Adjust Center: "));
            levelLabel.id = "levelLabel" + (filtersArray.length - 1); //index of filter in array
            newFilterDiv.appendChild(levelLabel);

            var levelSlider = document.createElement("input");
            levelSlider.id = "level" + (filtersArray.length - 1); //index of filter in array
            levelSlider.type = "range";
            levelSlider.min = -10;
            levelSlider.max = 10;
            levelSlider.value = 0;

            newFilterDiv.appendChild(levelSlider);

            var zeroButton = document.createElement("button");
                        zeroButton.id = "zero" + (filtersArray.length - 1); //index of filter in array
                        zeroButton.appendChild(document.createTextNode("Zero Level"));
                        zeroButton.classList.add("filterButton"); //set the style
                        zeroButton.tag = filtersArray.length - 1;
                        zeroButton.onclick = function(){
                            document.getElementById("level" + this.tag).value = 0;
                            showValue();
                            testFilters();
                            return false;
                          };
            newFilterDiv.appendChild(zeroButton);

            filterDiv.appendChild(newFilterDiv);
            levelSlider.addEventListener("input", showValue);
            levelSlider.addEventListener("change", testFilters);
            break;
        case "Rectify":
            var levelLabel = document.createElement("p");
            levelLabel.appendChild(document.createTextNode("Level: "));
            levelLabel.id = "levelLabel" + (filtersArray.length - 1); //index of filter in array
            newFilterDiv.appendChild(levelLabel);

            var levelSlider = document.createElement("input");
            levelSlider.id = "level" + (filtersArray.length - 1); //index of filter in array
            levelSlider.type = "range";
            levelSlider.min = -100;
            levelSlider.max = 100;
            levelSlider.value = 0;

            newFilterDiv.appendChild(levelSlider);

            var zeroButton = document.createElement("button");
                        zeroButton.id = "zero" + (filtersArray.length - 1); //index of filter in array
                        zeroButton.appendChild(document.createTextNode("Zero Level"));
                        zeroButton.classList.add("filterButton"); //set the style
                        zeroButton.tag = filtersArray.length - 1;
                        zeroButton.onclick = function(){
                            document.getElementById("level" + this.tag).value = 0;
                            showValue();
                            testFilters();
                            return false;
                          };
            newFilterDiv.appendChild(zeroButton);

            filterDiv.appendChild(newFilterDiv);

            levelSlider.addEventListener("input", showValue);
            levelSlider.addEventListener("change", testFilters);

            break;
        case "Gradient":
        var levelLabel = document.createElement("p");
                    levelLabel.appendChild(document.createTextNode("Level: "));
                    levelLabel.id = "levelLabel" + (filtersArray.length - 1); //index of filter in array
                    newFilterDiv.appendChild(levelLabel);

                    var levelSlider = document.createElement("input");
                    levelSlider.id = "level" + (filtersArray.length - 1); //index of filter in array
                    levelSlider.type = "range";
                    levelSlider.min = 1;
                    levelSlider.max = 25;
                    levelSlider.value = 10;

                    newFilterDiv.appendChild(levelSlider);

                    filterDiv.appendChild(newFilterDiv);
                    levelSlider.addEventListener("input", showValue);
                    levelSlider.addEventListener("change", testFilters);
            break;
        default:
            break;

    }
    //update the text labels
    showValue();
    testFilters();
}

function showValue()
{
    for (var filterIndex = 0; filterIndex < filtersArray.length; filterIndex++){
    var currentFilter = filtersArray[filterIndex];
        switch (currentFilter.type){
              case "High Pass":
              case "Low Pass":
                  document.getElementById("cutoffLabel" + filterIndex).innerHTML =
                            "Cutoff: " + document.getElementById("cutoff" + filterIndex).value;
                  document.getElementById("smoothingLabel" + filterIndex).innerHTML =
                            "Smoothing: " + document.getElementById("smoothing" + filterIndex).value;
                  break;
              case "Normalize":
                  document.getElementById("levelLabel" + filterIndex).innerHTML =
                            "Level: " + document.getElementById("level" + filterIndex).value + "%";
                  break;
              case "Rectify":
                  document.getElementById("levelLabel" + filterIndex).innerHTML =
                            "Level: " + document.getElementById("level" + filterIndex).value + "%";
                  break;
              case "Gradient":
                  document.getElementById("levelLabel" + filterIndex).innerHTML =
                            "Level: " + document.getElementById("level" + filterIndex).value + "%";
                  break;
              default:
                  break;


        }
    }
    //console.log("slider is changing: " + document.getElementById("cutoff" + 0).value);


}

function testFilters(){
    console.log("Testing Filters...");
    getEnabledSensors();
    gradientDivSize = 0;
    //reset filtered data to raw data
    FilteredDevicesArray = cloneDevicesArray(RawDevicesArray);
    console.log("Raw data should be below...");
    console.log(FilteredDevicesArray);
    //update filter values from user input
    upgradeFilters();
    //console.log(filtersArray);

    //apply filters to enabled data and save in FilteredDevicesArray

    //loop through all the devices
    for (var deviceIndex = 0;  deviceIndex < FilteredDevicesArray.length; deviceIndex++) {

        //loop through all the sensors on each device
        for (var sensorIndex = 1;  sensorIndex < FilteredDevicesArray[deviceIndex].data.length; sensorIndex++) {

            //if this sensor is enabled..
            if(FilteredDevicesArray[deviceIndex].enabledSensors[sensorIndex-1]){
               var tempArray = [];
               //Loop through and apply all filters to this sensors data
               for (var filterIndex=0; filterIndex < filtersArray.length; filterIndex++){
                       var currentFilter = filtersArray[filterIndex];
                       console.log(currentFilter);
                       switch (currentFilter.type){
                              case "High Pass":

                                  tempArray = highPassFilter(
                                          FilteredDevicesArray[deviceIndex].data[sensorIndex],
                                          FilteredDevicesArray[deviceIndex].data[0], //timestamps for sensor
                                          currentFilter.cutoff,
                                          currentFilter.smoothing);
                                  FilteredDevicesArray[deviceIndex].data[sensorIndex] = tempArray;
                                  break;
                              case "Low Pass":
                                  tempArray = lowPassFilter(
                                          FilteredDevicesArray[deviceIndex].data[sensorIndex],
                                          FilteredDevicesArray[deviceIndex].data[0], //timestamps for sensor
                                          currentFilter.cutoff,
                                          currentFilter.smoothing);
                                  FilteredDevicesArray[deviceIndex].data[sensorIndex] = tempArray;
                                  break;
                              case "Normalize":
                                  tempArray = normalize(FilteredDevicesArray[deviceIndex].data[sensorIndex],
                                  currentFilter.level);
                                  FilteredDevicesArray[deviceIndex].data[sensorIndex] = tempArray;
                                  break;
                              case "Rectify":
                                  tempArray = rectify(
                                        FilteredDevicesArray[deviceIndex].data[sensorIndex],
                                        currentFilter.level);
                                  FilteredDevicesArray[deviceIndex].data[sensorIndex] = tempArray;
                                  break;
                              case "Gradient":

                                  gradient(
                                  FilteredDevicesArray[deviceIndex].data[sensorIndex],
                                    currentFilter.level);
                                  break;
                              default:
                                  break;
                       }



                   }




            }
        }

    }

    //update graph
    updateGraph(true);
}

function exportFilters(){
    var hasGradient = false;
    var exportType = outputTypeCombobox.options[outputTypeCombobox.selectedIndex].text;

    for (var filterIndex = 0; filterIndex < filtersArray.length; filterIndex++){
        var currentFilter = filtersArray[filterIndex];
        if (currentFilter.type == "Gradient"){
            hasGradient = true;
        }
    }
    //first check if there is a gradient filter to save
    if (hasGradient){
        //Get confirmation that the user is ready to save
        if (confirm("Are you sure you want to export this filter setting as '" + exportType +"'? It will overwrite any existing values!") == true) {
            //save gradient filter
            showLoader(true);
            saveGradient();
            applyGradient(exportType);
            exportToTrack(exportType);
            gradientDivSize = 1;
            updateGraph(true);
            showLoader(false);

        } else { //canceled

        }
    } else {
        alert("You need a Gradient filter to export!");
    }
}

function upgradeFilters(){
    //update filtersArray with values from filter div
    for (var filterIndex = 0; filterIndex < filtersArray.length; filterIndex++){
        var currentFilter = filtersArray[filterIndex];
        switch (currentFilter.type){
               case "High Pass":
               case "Low Pass":
                   filtersArray[filterIndex].cutoff =
                        document.getElementById("cutoff" + filterIndex).value;
                   filtersArray[filterIndex].smoothing =
                        document.getElementById("smoothing" + filterIndex).value;
                   break;
               case "Normalize":
               case "Rectify":
               case "Gradient":
                   filtersArray[filterIndex].level =
                        document.getElementById("level" + filterIndex).value;
                   break;

               default:
                   break;
        }
    }
}

function discardFilters(){
    filterDiv.innerHTML = "";
    filtersArray = [];
    gradientDivSize = 0;
    updateGraph(false);
}

function cloneDevicesArray(arrayToClone){
    var tempDevicesArray = [];

    for (var deviceIndex = 0; deviceIndex < arrayToClone.length; deviceIndex++){
            var newSensor = {};
        newSensor.name = arrayToClone[deviceIndex].name;
        newSensor.alias = arrayToClone[deviceIndex].alias;
        newSensor.address = arrayToClone[deviceIndex].address;
        newSensor.data = [];
        for (var sensorIndex = 0; sensorIndex < arrayToClone[deviceIndex].data.length; sensorIndex++){
            var tempData = []
            for (var dataIndex = 0; dataIndex < arrayToClone[deviceIndex].data[sensorIndex].length; dataIndex++){
                var value = arrayToClone[deviceIndex].data[sensorIndex][dataIndex]
                tempData.push(value);
            }
            newSensor.data.push(tempData);
        }
        newSensor.enabledSensors = arrayToClone[deviceIndex].enabledSensors.slice();

        tempDevicesArray.push(newSensor);
    }
    return tempDevicesArray;

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                   FILTERS                                                 ///////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function highPassFilter( values, timestamps, cutoff, smoothing ){
 var value = values[0]; // start with the first value
 var newValues = [];
  for (var passes = 0; passes < smoothing; passes++ ){
  //re-applies the filter with a cuttof of 2 for smoothing passes
      for (var i=1, len=values.length; i<len; ++i){
        var currentValue = values[i];
        value =  (value + values[i] - values[i-1]) / cutoff
        newValues[i] = value;
      }
  cutoff = 2; //set to 2 for smoothing passes
  }
  return newValues;
}

function lowPassFilter( values, timestamps, cutoff, smoothing ){
  for (var passes = 0; passes < smoothing; passes++ ){
  var value = values[0]; // start with the first value
      for (var i=1, len=values.length; i<len; ++i){
        var currentValue = values[i];
        value += (currentValue - value) / cutoff;
        values[i] = value;
      }
      cutoff = 2;
  }
  return values;
}



function normalize(values, adjust){
    var avg = getAvg(values);
    for (var i=0, len=values.length; i<len; ++i){
        values[i] -= avg;
    }
    var max = getMax(values);
    var adjust = max * (adjust/100);
    for (var i=0, len=values.length; i<len; ++i){
        values[i] += adjust;
    }
    return values;
}

function rectify(values, level){
      var max = getMax(values);
      level = max * (level/100);
      for (var i=0, len=values.length; i<len; ++i){
          if (values[i] < level){
                values[i] = values[i] * -1;
          }
      }
  return values;
}

function gradient(values, scale){
    var max = getMax(values);
    var divSize = max * (scale/100);
    if (divSize > gradientDivSize){
            gradientDivSize = divSize;
    }
}

function saveGradient(){
    //loop through all the devices
    for (var deviceIndex = 0;  deviceIndex < FilteredDevicesArray.length; deviceIndex++) {
        //loop through all the sensors on each device
        for (var sensorIndex = 1;  sensorIndex < FilteredDevicesArray[deviceIndex].data.length; sensorIndex++) {
            //if this sensor is enabled..
            if(FilteredDevicesArray[deviceIndex].enabledSensors[sensorIndex-1]){
               var tempArray = [];
               tempArray = toGradient(FilteredDevicesArray[deviceIndex].data[sensorIndex],
                          gradientDivSize);
               FilteredDevicesArray[deviceIndex].data[sensorIndex] = tempArray;
            }
        }
    }
}

function toGradient(values, divSize){
    for (var i=0, len=values.length; i<len; ++i){
          for (var gradValue = 0; gradValue < 10; gradValue++){
                if (values[i] > gradValue * divSize && values[i] <= (gradValue + 1) * divSize){
                    values[i] = gradValue;
                    break;
                } else if (values [i] < -(gradValue * divSize) && values[i] >= -((gradValue + 1) * divSize)){
                    values[i] = gradValue;
                    break;
                }
          }
          if (values[i]>9 || values[i]<0){
                values[i] = 9;
          }
    }
    return values
}


function getMax (arrayValues){
var max = 0;
for (var i=0, len=arrayValues.length; i<len; ++i){
          if (arrayValues[i] > max){
                max = arrayValues[i];
          }
      }
return max;
}

function getAvg(values){
    var sum = 0
    var datapoints = 0
    for (var i=0, len=values.length; i<len; ++i){
          if (!isNaN(values[i])){
              sum += values[i];
              datapoints++;
          }
    }
    return sum/datapoints;
}

function getMaxFromInterval(values, timestamps, start, end){
    var max = 0;

    for (var i=0, len=values.length; i<len; ++i){
          if (timestamps[i] > start && timestamps[i] < end){
                if (values[i] > max){
                    max = values[i];
                }

          }
    }
    return max;
}

function overwriteInterval(values, timestamps, start, end, newValue){
    var max = 0;

    for (var i=0, len=values.length; i<len; ++i){
          if (timestamps[i] > start && timestamps[i] < end){
                values[i] = newValue;
          }
    }
}



function applyGradient(gradientType){
    for (var segmentNo = 0, len=trackArray.length; segmentNo<len; segmentNo++){
             var segment = trackArray[segmentNo];
             var segmentStart = new Date(segment.startTimestamp);
             var segmentEnd = new Date(segment.endTimestamp);
             var segmentArray = [];
             //loop through all the devices
             for (var deviceIndex = 0;  deviceIndex < FilteredDevicesArray.length; deviceIndex++) {
                //loop through all the sensors on each device
                for (var sensorIndex = 1;  sensorIndex < FilteredDevicesArray[deviceIndex].data.length; sensorIndex++) {
                    //if this sensor is enabled..
                    if(FilteredDevicesArray[deviceIndex].enabledSensors[sensorIndex-1]){
                        segmentArray.push(getMaxFromInterval(
                                FilteredDevicesArray[deviceIndex].data[sensorIndex],
                                 FilteredDevicesArray[deviceIndex].data[0], //timestamps for sensor
                                 segmentStart,
                                 segmentEnd));
                    }
                }
             }
             var segmentMax = getMax(segmentArray);

             for (var deviceIndex = 0;  deviceIndex < FilteredDevicesArray.length; deviceIndex++) {
                 //loop through all the sensors on each device
                 for (var sensorIndex = 1;  sensorIndex < FilteredDevicesArray[deviceIndex].data.length; sensorIndex++) {
                     //if this sensor is enabled..
                     if(FilteredDevicesArray[deviceIndex].enabledSensors[sensorIndex-1]){

                          overwriteInterval(FilteredDevicesArray[deviceIndex].data[sensorIndex],
                                  FilteredDevicesArray[deviceIndex].data[0], //timestamps for sensor
                                  segmentStart,
                                  segmentEnd,
                                  segmentMax);
                     }
                 }
             }
             //update the difficulty of the segment if necessary
             if (segmentMax > trackArray[segmentNo].difficulty){
                trackArray[segmentNo].difficulty = segmentMax;
             }


             switch (gradientType){

                     case "Steepness":
                          trackArray[segmentNo].steepness = segmentMax
                          break;
                     case "Roughness":
                          trackArray[segmentNo].roughness = segmentMax
                          break;
                     case "Roll":
                          trackArray[segmentNo].roll = segmentMax
                          break;
                     default:
                         break;
             }
    }

}

function exportToTrack(gradientType){
    console.log("exporting segments to: " + gradientType);
    //for each GPS track segment
    for (var segmentNo = 0, len=trackArray.length; segmentNo<len; segmentNo++){
         //update the overall difficulty
         trackRef.child(segmentNo).update({difficulty: trackArray[segmentNo].difficulty});
         //then update the changed difficulty
         switch (gradientType){
            case "Steepness":
                trackRef.child(segmentNo).update({steepness: trackArray[segmentNo].steepness});
                 break;
            case "Roughness":
                trackRef.child(segmentNo).update({roughness: trackArray[segmentNo].roughness});
                 break;
            case "Roll":
                trackRef.child(segmentNo).update({roll: trackArray[segmentNo].roll});
                 break;
            default:
                break;
         }
    }
}


