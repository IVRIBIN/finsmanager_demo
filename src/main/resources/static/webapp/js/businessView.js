/*
    Created by Ivribin
*/

let hrefControl;

function StartPage() {
    hrefControl = $("#hrefControlHeaderId").attr("href");
    doAjaxBusinessRepository();
}

function GetTableArrRows(tableName){
    arrRows = [];
    if(tableName == "SubUser"){
        arrRows = [
            {cell_control:"id",cell_type:"sys_field"},
            {cell_control:"fio",cell_type:"field"},
        ];
    }
    if(tableName == "Counteragent"){
            arrRows = [
                {cell_control:"id",cell_type:"sys_field"},
                {cell_control:"name",cell_type:"field"},
            ];
        }
    return arrRows;
}


//Генерация плитки бизнесов
function SetBusinessList(arr){
    sHrefControl = $("#IdDivHrefControl").html()
    var htmlBusinessList = "";
    arr.forEach(function(item, i, arr) {
        var strActiveClass = "";
        if(item.activeFlg == "Y"){strActiveClass="active-business"}else{strActiveClass=""};
        htmlBusinessList += `<div class="col-12 col-md-4 col-sm-6"><div class="box ${strActiveClass} tech_business_box" record_id="${item.id}"><div class="box-body text-center"><div class="box-title mb-3"><h3>${item.name}</h3></div><div class="box-text mb-3 text-truncate text-muted fw-light">${item.description}</div><div class="box-link"><a onclick="ConfigBusinessFormActivation(${item.id});" class="btn btn-modify-action-border btn-modify-action-size" href="${sHrefControl}" role="button"><i class='bx bxs-grid'></i> Контроль</a></div></div><div class="box-edit"><a onclick="GetBusinessForm(${item.id});" href="#" class="btn fw-light btn-sm" role="button" data-bs-toggle="modal" data-bs-target="#fromCreateBus"><i class='bx bx-edit'></i> Изменить</a> <button onclick="ConfigBusinessFormDelete(${item.id});" class="btn fw-light btn-sm" type="button"><i class="bx bx-trash"></i> Удалить</button></div></div></div>`;
    });
    $("#businessListId").html(htmlBusinessList);
}

//Подготовка формы новой записи
function ConfigBusinessFormNew(){
    CleanForm("formBusinessId",GetArrFields("formBusiness"));
    ResetFormDvm("formBusinessId",GetArrFields("formBusiness"))
    $('#formBusinessId').find('[form_control="method"]').val("insert");
}
//Подготовка формы удаления записи
function ConfigBusinessFormDelete(Id){
    var form = {};
    form["method"]="delete";
    form["id"]=Id;
    doAjaxFormBusiness(form);
}

//Подготовка формы активации бизнеса
function ConfigBusinessFormActivation(Id){
    var form = {};
    form["method"]="set_active";
    form["id"]=Id;
    doAjaxFormBusiness(form);
}

//Запрос данных формы и заполнение данных
function GetBusinessForm(rowId){
    CleanForm("formBusinessId",GetArrFields("formBusiness"));
    $("#formBusinessId").find('[form_control="method"]').val("select");
    $("#formBusinessId").find('[form_control="id"]').val(rowId);
    doAjaxFormBusiness(GetFormData("formBusinessId", GetArrFields("formBusiness")));
    $("#formBusinessId").find('[form_control="method"]').val("update");
}


//Получить массив полей формы
function GetArrFields(formName){
    arrFields = [];
    if(formName == "formBusiness"){
        arrFields = [{form_control:"method",control_type:"sys_field"},{form_control:"id",control_type:"field"},{form_control:"name",control_type:"field"},{form_control:"nameDvm",control_type:"Dvm",parent_field:"name"},{form_control:"description",control_type:"field"},{form_control:"descriptionDvm",control_type:"Dvm",parent_field:"description"}];
    }
    return arrFields;
}

//Событие выбора бизнеса
$(function(){
    $("#businessListId").on("click", ".tech_business_box", function () {
        ConfigBusinessFormActivation($(this).attr("record_id"));
    });
});

//Запрос списка SubUser пользователя
function GetSubUserShortList() {
    var form = {};
    form["method"] = "getSubUserListShort";
    doAjaxAppUserRequest(form);
}

//Запрос списка SubUser бизнеса
function GetSubUserShortBList() {
    var form = {};
    form["method"] = "GetSubUserShortBList";
    doAjaxAppUserRequest(form);
}

//Запрос списка Контрагентов
function GetCounteragentShortList() {
    var form = {};
    form["method"] = "getAll";
    doAjaxRequestCounteragent(form);
}



//Запрос списка Контрагентов бизнеса
function GetCounteragentShortBList() {
    var form = {};
    form["method"] = "getAllB";
    doAjaxRequestCounteragent(form);
}

//------------------Обертка форм апплета доступа Пользователя к бизнесу------------------
    //Событие выбора строки таблицы доступа к проекту (Все сотрудники) (помимо обработчика в common.js)
    $(function(){
        $("table[data_table='#tableAllSubUserListId']").find("tbody").on("click", "tr", function () {
            SetAccessButtonBusinessSubUser();
        });
    });

    //Событие выбора строки таблицы доступа к проекту (Все сотрудники) (помимо обработчика в common.js)
    $(function(){
        $("table[data_table='#tableAllSubUserBListId']").find("tbody").on("click", "tr", function () {
            SetAccessButtonBusinessSubUser();
        });
    });

    //Доступ кнопок
    function SetAccessButtonBusinessSubUser() {
        if($("#tableAllSubUserListId").find("tbody").find(".bg-light").attr("rowid") == undefined){
            $("#AddSubUserToBusinessId").attr("disabled",true);
        }else{
            $("#AddSubUserToBusinessId").attr("disabled",false);
        }
        if($("#tableAllSubUserBListId").find("tbody").find(".bg-light").attr("rowid") == undefined){
            $("#DeleteSubUserFromBusinessId").attr("disabled",true);
        }else{
            $("#DeleteSubUserFromBusinessId").attr("disabled",false);
        }
    }

    //Добавить SubUser в бизнес
    function AddSubUserToBusiness(){
        var rowId = $("#tableAllSubUserListId").find("tbody").find(".bg-light").attr("rowid");
        if(rowId != undefined && rowId != ""){
            var form = {};
            form["method"] = "AddSubUserToActiveBusiness";
            form["id"] = rowId;
            doAjaxAppUserRequest(form);
        }
    }

    //Удалить SubUser из бизнеса
    function DeleteSubUserFromBusiness(){
        var rowId = $("#tableAllSubUserBListId").find("tbody").find(".bg-light").attr("rowid");
        if(rowId != undefined && rowId != ""){
            var form = {};
            form["method"] = "DeleteSubUserFromBusiness";
            form["id"] = rowId;
            doAjaxAppUserRequest(form);
        }
    }
//---------------------------------------------------------------------------
//------------------Обертка форм апплета доступа Контрагента к бизнесу------------------

    //Событие выбора строки таблицы доступа к проекту (Все контрагенты) (помимо обработчика в common.js)
    $(function(){
        $("table[data_table='#tableAllCounteragentListId']").find("tbody").on("click", "tr", function () {
            SetAccessButtonBusinessCounteragent();
        });
    });

    //Событие выбора строки таблицы доступа к проекту (Контрагенты проекта) (помимо обработчика в common.js)
    $(function(){
        $("table[data_table='#tableAllCounteragentBListId']").find("tbody").on("click", "tr", function () {
            SetAccessButtonBusinessCounteragent();
        });
    });

    //Доступ кнопок
    function SetAccessButtonBusinessCounteragent() {
        if($("#tableAllCounteragentListId").find("tbody").find(".bg-light").attr("rowid") == undefined){
            $("#AddCounteragentToBusinessId").attr("disabled",true);
        }else{
            $("#AddCounteragentToBusinessId").attr("disabled",false);
        }
        if($("#tableAllCounteragentBListId").find("tbody").find(".bg-light").attr("rowid") == undefined){
            $("#DeleteCounteragentFromBusinessId").attr("disabled",true);
        }else{
            $("#DeleteCounteragentFromBusinessId").attr("disabled",false);
        }
    }

    //Добавить Counteragent в бизнес
    function AddCounteragentToBusiness(){
        var rowId = $("#tableAllCounteragentListId").find("tbody").find(".bg-light").attr("rowid");
        if(rowId != undefined && rowId != ""){
            var form = {};
            form["method"] = "AddCounteragentToActiveBusiness";
            form["id"] = rowId;
            doAjaxRequestCounteragent(form);
        }
    }

    //Удалить Counteragent из бизнеса
    function DeleteCounteragentFromBusiness(){
        var rowId = $("#tableAllCounteragentBListId").find("tbody").find(".bg-light").attr("rowid");
        if(rowId != undefined && rowId != ""){
            var form = {};
            form["method"] = "DeleteCounteragentFromBusiness";
            form["id"] = rowId;
            doAjaxRequestCounteragent(form);
        }
    }
//---------------------------------------------------------------------------

//-----------Ajax Functions------------
function doAjaxFormBusiness(businessForm) {
    var formMethod = businessForm.method;
    $.ajax({
        url : 'formBusiness',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : JSON.stringify(businessForm),
        success: function (data) {
            if(formMethod == "select"){
                FormDataToWebForm("formBusinessId",data.businessEx,GetArrFields("formBusiness"));
                SetFormValid("formBusinessId",GetArrFields("formBusiness"))
            }
            if(formMethod == "set_active" || formMethod == "delete"){
                doAjaxBusinessRepository();
            }
            if(formMethod == "insert" || formMethod == "update"){
                FormValidationToWebForm("formBusinessId",data.businessExImp,GetArrFields("formBusiness"));
                doAjaxBusinessRepository();
            }
        }
    });
}

function doAjaxBusinessRepository() {
   $.ajax({
        url : 'BusinessRepository',
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : ({
            Operation : 'getUserBusinessList'
        }),
        success: function (data) {
            var arr = data.businessList;
            SetBusinessList(arr);
        }
    });
}

function doAjaxAppUserRequest(appUserRequest) {
    var formMethod = appUserRequest.method;
    //SpinnerOn("doAjaxAppUserRequest");
    $.ajax({
        url: 'requestAppUser',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data: JSON.stringify(appUserRequest),
        success: function (data) {
            //SpinnerOff("doAjaxAppUserRequest");
            if (formMethod == "getSubUserListShort") {
                $('#tableAllSubUserListId').find("tbody").html(DataToWebTableBody(data.appUserExListAbstraction, GetTableArrRows("SubUser")));
                SetAccessButtonBusinessSubUser();
            }
            if (formMethod == "GetSubUserShortBList") {
                $('#tableAllSubUserBListId').find("tbody").html(DataToWebTableBody(data.appUserExListAbstraction, GetTableArrRows("SubUser")));
                SetAccessButtonBusinessSubUser();
            }
            if(formMethod == "AddSubUserToActiveBusiness" || formMethod == "DeleteSubUserFromBusiness"){
                GetSubUserShortBList();
            }

        }
    });
}


function doAjaxRequestCounteragent(requestCounteragent) {
    var formMethod = requestCounteragent.method;
    $.ajax({
        url : 'requestCounteragent',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : JSON.stringify(requestCounteragent),
        success: function (data) {
            if(formMethod == "getAll"){
                $('#tableAllCounteragentListId').find("tbody").html(DataToWebTableBody(data.counteragentExList, GetTableArrRows("Counteragent")));
            }
            if(formMethod == "getAllB"){
                $('#tableAllCounteragentBListId').find("tbody").html(DataToWebTableBody(data.counteragentExList, GetTableArrRows("Counteragent")));
            }
            if(formMethod == "AddCounteragentToActiveBusiness" || formMethod == "DeleteCounteragentFromBusiness"){
                GetCounteragentShortBList();
            }
        }
    });
}