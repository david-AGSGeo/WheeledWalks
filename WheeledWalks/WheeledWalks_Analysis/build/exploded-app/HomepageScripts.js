var rawDataRef = new Firebase('https://wheeledwalks.firebaseio.com/Raw_Walks/Walks');
var infoDataRef = new Firebase('https://wheeledwalks.firebaseio.com/Walk_Info/Walks');



      infoDataRef.on('child_added', function(snapshot) {
            var walk = snapshot.val();
            $('#loading').hide();
            console.log("adding walk: " + walk.name);
            displayWalkCard(walk);
      });

      function displayWalkCard(walk) {

      console.log("adding walk card: " + walk.name);
      var wcDiv = document.getElementById("WalkCardDiv"); //get the root element to add walks to

      var cardDiv = document.createElement("div"); //create the card background
      cardDiv.classList.add("walkCardBackground"); //set the style

      var textDiv = document.createElement("div"); //create the text Div
            textDiv.classList.add("walkCardText"); //set the style


      var wcTitle = document.createElement("h2");
      wcTitle.appendChild(document.createTextNode(walk.name));
      wcTitle.align = "center";
      cardDiv.appendChild(wcTitle);

      var wcLocality = document.createElement("h4");
            wcLocality.appendChild(document.createTextNode("Locality: " + walk.locality));
            textDiv.appendChild(wcLocality);
            var walkDate = new Date(walk.dateSurveyed);
             var wcDate = document.createElement("h4");
                        wcDate.appendChild(document.createTextNode("Date Surveyed: " + walkDate));
                        textDiv.appendChild(wcDate);
      var wcDistance = document.createElement("h4");
                  wcDistance.appendChild(document.createTextNode("Distance: " + walk.length.toFixed(1) + " meters"));
                  textDiv.appendChild(wcDistance);
      var wcGrade = document.createElement("h4");
                  wcGrade.appendChild(document.createTextNode("Subjective Difficulty Grade: " + walk.grade + "/5"));
                  textDiv.appendChild(wcGrade);
      var wcRating = document.createElement("h4");
                  wcRating.appendChild(document.createTextNode("Star Rating: " + walk.rating + "/5"));
                  textDiv.appendChild(wcRating);
      var wcDescription = document.createElement("p");
                  wcDescription.appendChild(document.createTextNode("Description: " + walk.description));
                  textDiv.appendChild(wcDescription);


      cardDiv.appendChild(textDiv);
      var wcImage = document.createElement("img");
                        wcImage.classList.add("walkCardImage"); //set the style
                        wcImage.src = "/ImageResources/Backgrounds/analysis_page.JPG" ;
                        wcImage.alt = "The Menu Image for " + walk.name + " cannot be found!";
                        cardDiv.appendChild(wcImage);
      //create the submit button for the form
      var wcDisplayWalkForm =document.createElement("form");
      //wcDisplayWalkForm.setAttribute('method',"get");
      wcDisplayWalkForm.setAttribute('action',"/DisplayWalk");
      var formWalkName = document.createElement("input"); //input element, text
      formWalkName.setAttribute('type',"hidden");
      formWalkName.setAttribute('name',"walk");
      formWalkName.setAttribute('value',walk.name);

      var formSubmitButton = document.createElement("input"); //input element, Submit button
      formSubmitButton.classList.add("greenButton"); //set the style
      formSubmitButton.setAttribute('type',"submit");
      formSubmitButton.setAttribute('value',"Process Walk Data");

      wcDisplayWalkForm.appendChild(formWalkName);
      wcDisplayWalkForm.appendChild(formSubmitButton);

      cardDiv.appendChild(wcDisplayWalkForm);

      wcDiv.appendChild(cardDiv);//add the new card to the list


            };

