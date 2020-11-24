>**ROOT localhost:8080/topjava**
----------------------------------------------------------------------------------
**POST  /rest/meals     create meal, no duplicates by dateTime for one user**
**URL Params:**
    None
**Data Params:**
    {   
     "dateTime":[LocalDateTime],         
     "description":[String],        
     "calories":[int]
    }
----------------------------------------------------------------------------------
**GET   /rest/meals     get all user meals**
**URL Params:**
    None
**Data Params:**
    None
----------------------------------------------------------------------------------
**DELETE    /rest/meals/{id}    delete meal by id**
**URL Params:**
    id
**Data Params:**
    None
----------------------------------------------------------------------------------
**PUT   /rest/meals/{id}        update meal by id**
**URL Params:**
    id
**Data Params:**
    {   
     "dateTime":[LocalDateTime],         
     "description":[String],        
     "calories":[int]
    }
----------------------------------------------------------------------------------
**GET   /rest/meals/{id}        get meal by id**
**URL Params:**
    id
**Data Params:**
    None
----------------------------------------------------------------------------------
**GET   /rest/meals/filter      get meal between date/time boarders**
**URL Params:**
    id
**Data Params:**
    Optional: 
    startDate=[LocalDate], 
    startTime=[LocalTime], 
    endDate=[LocalDate], 
    endTime=[LocalTime]
