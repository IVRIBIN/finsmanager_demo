/*
    Created by Ivribin
*/

let sUserRoleGlobal = "";


function StartPage(){
    //Сначала запрашиваем роль пользователя
    doAjaxRequestUserCache();
}

function StartPagePost() {
    SetTableAccessBySelectRow("#tableEmployeeId","","");
    $('#filterEmployeeId').find('input[form_control="offset"]').val("0");
    $('#filterEmployeeId').find('input[form_control="limit"]').val("3");
    doAjaxRequestCompany();
    doAjaxRequestEmployee("getCompanyEmployeeList");
    doAjaxRequestEmployee("getSelectedCompanyEmployeeCount");
    GetFinsInfo();
}



//Получить массив полей формы
function GetArrFields(formName){
    arrFields = [];
    if(formName == "formCompany"){
        arrFields = [{form_control:"method",control_type:"sys_field"},{form_control:"id",control_type:"field"},
            {form_control:"name",control_type:"field"},{form_control:"nameDvm",control_type:"Dvm",parent_field:"name"},
            {form_control:"description",control_type:"field"},{form_control:"descriptionDvm",control_type:"Dvm",parent_field:"description"},
            {form_control:"inn",control_type:"field"},{form_control:"innDvm",control_type:"Dvm",parent_field:"inn"},
            {form_control:"kpp",control_type:"field"},{form_control:"kppDvm",control_type:"Dvm",parent_field:"kpp"},
            {form_control:"account",control_type:"field"},{form_control:"accountDvm",control_type:"Dvm",parent_field:"account"}];
    }
    if(formName == "formEmployee"){
        arrFields = [{form_control:"method",control_type:"sys_field"},{form_control:"id",control_type:"field"},
            {form_control:"firstName",control_type:"field"},{form_control:"firstNameDvm",control_type:"Dvm",parent_field:"firstName"},
            {form_control:"lastName",control_type:"field"},{form_control:"lastNameDvm",control_type:"Dvm",parent_field:"lastName"},
            {form_control:"middleName",control_type:"field"},{form_control:"middleNameDvm",control_type:"Dvm",parent_field:"middleName"},
            {form_control:"position",control_type:"pickList"},{form_control:"positionDvm",control_type:"Dvm",parent_field:"position"},
            {form_control:"phone",control_type:"field"},{form_control:"phoneDvm",control_type:"Dvm",parent_field:"phone"},
            {form_control:"account",control_type:"field"},{form_control:"accountDvm",control_type:"Dvm",parent_field:"account"},
            {form_control:"balance",control_type:"field"},{form_control:"balanceDvm",control_type:"Dvm",parent_field:"balance"},

            {form_control:"accName",control_type:"field"},{form_control:"accNameDvm",control_type:"Dvm",parent_field:"accName"},
            {form_control:"description",control_type:"field"},{form_control:"descriptionDvm",control_type:"Dvm",parent_field:"description"},
            {form_control:"cardNum",control_type:"field"},{form_control:"cardNumDvm",control_type:"Dvm",parent_field:"cardNum"},
            {form_control:"inn",control_type:"field"},{form_control:"innDvm",control_type:"Dvm",parent_field:"inn"},
            {form_control:"kpp",control_type:"field"},{form_control:"kppDvm",control_type:"Dvm",parent_field:"kpp"},
            {form_control:"bik",control_type:"field"},{form_control:"bikDvm",control_type:"Dvm",parent_field:"bik"},
            {form_control:"bankName",control_type:"field"},{form_control:"bankNameDvm",control_type:"Dvm",parent_field:"bankName"},
            ];
    }
    if(formName == "filterEmployee"){
        arrFields = [{form_control:"initFlg",control_type:"checkBox"},{form_control:"fullName",control_type:"field"},
            {form_control:"fullNameDvm",control_type:"Dvm",parent_field:"fullName"},{form_control:"position",control_type:"pickList"},
            {form_control:"positionDvm",control_type:"Dvm",parent_field:"position"},{form_control:"orderBy",control_type:"field"},{form_control:"orderLevel",control_type:"field"},
            {form_control:"limit",control_type:"field"},{form_control:"offset",control_type:"field"}];
    }
    return arrFields;
}

function GetTableArrRows(tableName){
    arrRows = [];
    if(tableName == "Employee"){
        arrRows = [
            {cell_control:"id",cell_type:"sys_field"},
            {cell_control:"rowNumber",cell_type:"field"},
            {cell_control:"selected",cell_type:"checkBox"},
            {cell_control:"firstName",cell_type:"field"},
            {cell_control:"lastName",cell_type:"field"},
            {cell_control:"middleName",cell_type:"field"},
            {cell_control:"position",cell_type:"pickList",value_code:{GenMng:"Генеральный директор",Cash:"Касса",Bookkeeper:"Бухгалтер",Specialist:"Специалист",Driver:"Водитель"}},
            {cell_control:"phone",cell_type:"field"},
            {cell_control:"account",cell_type:"field"},
            {cell_control:"balance",cell_type:"field"}
        ];
    }
    return arrRows;
}

//Запрос данных формы и заполнение данных Компании
function GetCompanyForm(){
    CleanForm("formCompanyId",GetArrFields("formCompany"));
    $("#formCompanyId").find('[form_control="method"]').val("select");
    doAjaxFormCompany(GetFormData("formCompanyId", GetArrFields("formCompany")));
    $("#formCompanyId").find('[form_control="method"]').val("update");
}

//Заполнить информационные поля компании
function SetCompanyInfo(data) {
    $("#id_company_name_info").html(data.name);
    $("#id_company_description_info").html(data.description);
    $("#id_company_inn_info").html(data.inn);
    $("#id_company_kpp_info").html(data.kpp);
    $("#id_company_account_info").html(data.account);
}

//Кнопка Создать таблицы работников. Подготовка формы новой записи Сотрудника
function ConfigEmployeeFormNew(){
    CleanForm("formEmployeeId",GetArrFields("formEmployee"));
    ResetFormDvm("formEmployeeId",GetArrFields("formEmployee"));
    $("#formEmployeeId").find('[form_control="method"]').val("insert");
}

//Кнопка Изменить таблицы работников
function ConfigEmployeeFormEdit(rowId) {
    //var rowId = $("#tableEmployeeId").find("tbody").find(".bg-light").attr("rowid");
    if(rowId){
        var form = {};
        form["method"] = "select";
        form["id"] = rowId;
        doAjaxFormEmployee(form);
        $("#formEmployeeId").find('[form_control="method"]').val("update");
    }
}

//Кнопка Удалить таблицы работников
function ConfigEmployeeFormDelete(rowId) {
    //var rowId = $("#tableEmployeeId").find("tbody").find(".bg-light").attr("rowid");
    if(rowId){
        var form = {};
        form["method"] = "delete";
        form["id"] = rowId;
        doAjaxFormEmployee(form);
    }
}

//Кнопка Удалить таблицы Выбранных работников
function ConfigEmployeeFormDeleteSelected() {
    var form = {};
    form["method"] = "delete_selected";
    doAjaxFormEmployee(form);
}

//Генерация таблицы
function DataToWebTableBodyCustom(TableData,UserRole) {
    var strTableBodyData = ""
    if(TableData != null) {
        for (var rowData of TableData) {
            strTableBodyData += `<div class="col-12"><div class="form-box"><div class="mt-2"><div class="row">`+
            `<div class="col-12 col-md-8 mb-3"><span class="w-100 text-truncate d-block fs-4 mb-1">${rowData.accName}</span>`+
            `<span class="w-100 text-truncate d-block fs-6">${rowData.description}</span><small class="fw-light">`+
            `<span>№ счета: ${rowData.account}</span>, <span>ИНН: ${rowData.inn}</span>, <span>КПП: ${rowData.kpp}</span>, `+
            `<span>БИК: ${rowData.bik}</span>, <span>№ карты: ${rowData.cardNum}</span></small></div><div class="col-4">`+
            `<span class="w-100 text-truncate d-block fs-3"><small>₽</small> ${rowData.balanceTotal}</span>`+
            `<small class="text-truncate text-muted d-block fw-light">На счете</small></div><div class="col-12 col-md-4 mt-2 mb-3">`+

            `${UserRole == "USER" ? `<button tbl_update="" onclick="ConfigEmployeeFormEdit(${rowData.id});" class="btn btn-modify-action-border btn-sm btn-margin-r-5" role="button" data-bs-toggle="modal" data-bs-target="#EmployeeFormModalToggle"><i class="bx bx-edit"></i>Изменить</button>` : "" }` +
            `${UserRole == "USER" ? `<button tbl_delete="" onclick="ConfigEmployeeFormDelete(${rowData.id});" class="btn btn-outline-danger btn-sm btn-margin-r-5" role="button"><i class="bx bx-trash"></i>Удалить</button>` : "" }` +

            `</div></div>`+
            `</div></div></div>`;
        }
    }
    return strTableBodyData;
}


//Запрос финс. инфо. по бизнесу
function GetFinsInfo() {
    var request = {};
    request["method"]="getFinsInfo";
    doAjaxRequestBusiness(request);
}

//Запрос компании
function GeеCompanyInfo() {
    var request = {};
    request["method"]="getFinsInfo";
    doAjaxRequestBusiness(request);
}

//-----------Ajax Functions------------
function doAjaxRequestCompany() {
    $.ajax({
        url : 'requestCompany',
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : ({
            Operation : 'getBusinessCompanyList'
        }),
        success: function (data) {
            if(data.companyExList != null){
                SetCompanyInfo(data.companyExList[0]);
            }
        }
    });
}

function doAjaxFormCompany(companyForm) {
    var formMethod = companyForm.method;
    $.ajax({
        url : 'formCompany',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : JSON.stringify(companyForm),
        success: function (data) {
            if(formMethod == "select"){
                FormDataToWebForm("formCompanyId",data.companyEx,GetArrFields("formCompany"));
                SetFormValid("formCompanyId",GetArrFields("formCompany"));
            }
            if(formMethod == "update"){
                FormValidationToWebForm("formCompanyId",data.companyExImpl,GetArrFields("formCompany"));
                doAjaxRequestCompany();
            }
        }
    });
}

function doAjaxFormEmployee(employeeForm) {
    var formMethod = employeeForm.method;
    $.ajax({
        url : 'formEmployee',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : JSON.stringify(employeeForm),
        success: function (data) {
            if(formMethod == "insert" || formMethod == "update"){
                FormValidationToWebForm("formEmployeeId",data.employeeExImpl,GetArrFields("formEmployee"));
                if(data.employeeExImpl.validFlg){doAjaxRequestEmployee("getCompanyEmployeeList");}
            }
            if(formMethod == "select"){
                FormDataToWebForm("formEmployeeId",data.employeeEx,GetArrFields("formEmployee"));
                SetFormValid("formEmployeeId",GetArrFields("formEmployee"));
            }
            if(formMethod == "delete" || formMethod == "delete_selected"){
                doAjaxRequestEmployee("getCompanyEmployeeList");
                doAjaxRequestEmployee("getSelectedCompanyEmployeeCount");
            }
            if(formMethod == "update_selected"){
                doAjaxRequestEmployee("getSelectedCompanyEmployeeCount");
            }

        }
    });
}

function doAjaxRequestEmployee(Operation) {
    var intOffset = $('#filterEmployeeId').find('input[form_control="offset"]').val();
    var intLimit = $('#filterEmployeeId').find('input[form_control="limit"]').val();
    var employeeFilterForm = GetFormData('filterEmployeeId',GetArrFields('filterEmployee'));
    employeeFilterForm["offset"]=intOffset;
    employeeFilterForm["limit"]=intLimit;
    employeeFilterForm["operation"]=Operation;
    $.ajax({
        url : 'requestEmployee',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : JSON.stringify(employeeFilterForm),
        success: function (data) {
            if(Operation == "getCompanyEmployeeList") {

                if(data.employeeFilter != null) {
                    FormValidationToWebForm("filterEmployeeId", data.employeeFilter, GetArrFields("filterEmployee"));
                }
                //$('#tableEmployeeId').find("tbody").html(DataToWebTableBody(data.employeeExImplList, GetTableArrRows("Employee")));

                $('#tableCompanyAccListId').html(DataToWebTableBodyCustom(data.employeeExImplList,sUserRoleGlobal));

                $('#tableEmployeeId').find("small[table_row_count]").html("Всего: " + data.employeeCount);
                $('#tableEmployeeId').find("small[table_row_count]").attr("value", data.employeeCount);
                SetTableAccessNavigation("#tableEmployeeId","#filterEmployeeId");
                SetTableAccessBySelectRow("#tableEmployeeId","","");
                $("#span_emploee_count_id").html(data.employeeCount);
            }
            if(Operation == "getSelectedCompanyEmployeeCount") {
                $('#tableEmployeeId').find("small[table_selected_count]").html("Выбрано: " + data.selectedCount);
                $('#tableEmployeeId').find("small[table_selected_count]").attr("value", data.selectedCount);
                SetTableAccessDeleteSelected("#tableEmployeeId",data.selectedCount);
            }
        }
    });
}

function doAjaxRequestBusiness(requestBusiness) {
    var formMethod = requestBusiness.method;
    $.ajax({
        url : 'formBusiness',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : JSON.stringify(requestBusiness),
        success: function (data) {
            if(formMethod == "getFinsInfo"){
                if(data.businessExImp != null){
                    $("#balance_total_field_id").html("₽ " + data.businessExImp.balanceTotal);
                }
            }
        }
    });
}

function doAjaxRequestUserCache() {
    $.ajax({
        url : 'usercache',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : '',
        success: function (data) {
            sUserRoleGlobal = data.userRole;
            StartPagePost();
        }
    });
}


