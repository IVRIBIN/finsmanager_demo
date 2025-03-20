
/*
    Created by Ivribin
*/

//Генерация JSON данных формы
function GetFormData(form_id,arrFields) {
    var form = {};
    for(var field of arrFields){
        if(field.control_type == "field" || field.control_type == "pickList" || field.control_type == "sys_field") {
            form[field.form_control] = $('#' + form_id).find('[form_control="' + field.form_control + '"]').val();
        }
        if(field.control_type == "checkBox") {
            form[field.form_control] = document.querySelector('#' + form_id + ' [form_control="' + field.form_control + '"]').checked;
        }
        if(field.control_type == "pickList") {
            let strVal = $('#' + form_id).find('[form_control="' + field.form_control + '"]').val();
            if(strVal == 'Выберете значение'){form[field.form_control]="none"}else{form[field.form_control] = strVal;};
        }
        if(field.control_type == "pickListDynamic") {
            let strVal = $('#' + form_id).find('[form_control="' + field.form_control + '"]').val();
            if(strVal == 'Выберете значение'){form[field.form_control]="none"}else{form[field.form_control] = strVal;};
        }
        if(field.control_type == "field_float") {
            let strVal = $('#' + form_id).find('[form_control="' + field.form_control + '"]').val();
            let floatVal = parseFloat(strVal).toFixed(2);
            if(floatVal == "NaN"){floatVal=0.00};
            form[field.form_control] = floatVal;
        }
    }
    return form;
}

//Заполнение полей формы
function FormDataToWebForm(form_id,formData,arrFields) {
    for(var field of arrFields) {
        if (field.control_type == "field") {
            $('#'+form_id).find('[form_control="' + field.form_control + '"]').val(formData[field.form_control]);
        }
        if (field.control_type == "field_float") {
            $('#'+form_id).find('[form_control="' + field.form_control + '"]').val(formData[field.form_control]);
        }
        if (field.control_type == "pickList") {
            $('#'+form_id).find('[form_control="' + field.form_control + '"]').prop('selected',false);
            $('#'+form_id).find('[form_control="' + field.form_control + '"]').find('option[value="'+ formData[field.form_control] +'"]').prop('selected',true);
        }
        if(field.control_type == "checkBox"){
            if(formData[field.form_control]){
                $('#'+form_id).find('[form_control="' + field.form_control + '"]').prop('checked', true);
            }else{
                $('#'+form_id).find('[form_control="' + field.form_control + '"]').prop('checked', false);
            }
        }
    }
}

//Чистка полей формы
function CleanForm(form_id,arrFields) {
    for(var field of arrFields){
        if(field.control_type=="pickList"){
            $('#'+form_id).find('[form_control="' + field.form_control + '"]').val("Выберете значение");
            $('#'+form_id).find('select[form_control="' + field.form_control + '"]').prop('selectedIndex',0);
        }else{
            $('#'+form_id).find('[form_control="' + field.form_control + '"]').val("");
        }

    }
}

//Результат валидации
function FormValidationToWebForm(form_id,formValidationData,arrFields) {
    for(var field of arrFields){
        if(field.control_type == "Dvm"){
            if (formValidationData[field.form_control] == null) {
                $('#' + form_id).find('[form_control="' + field.form_control + '"]').addClass("d-none");
                $('#' + form_id).find('[form_control="' + field.parent_field + '"]').addClass("is-valid");
                $('#' + form_id).find('[form_control="' + field.parent_field + '"]').removeClass("is-invalid");

            } else {
                $('#' + form_id).find('[form_control="' + field.form_control + '"]').removeClass("d-none");
                $('#' + form_id).find('[form_control="' + field.parent_field + '"]').removeClass("is-valid");
                $('#' + form_id).find('[form_control="' + field.parent_field + '"]').addClass("is-invalid");
                $('#' + form_id).find('[form_control="' + field.form_control + '"]').html(formValidationData[field.form_control]);
            }
        }
    }
}

function SetFormValid(form_id,arrFields) {
    for(var field of arrFields){
        if(field.control_type == "Dvm"){
            $('#' + form_id).find('[form_control="' + field.form_control + '"]').addClass("d-none");
            $('#' + form_id).find('[form_control="' + field.parent_field + '"]').addClass("is-valid");
            $('#' + form_id).find('[form_control="' + field.parent_field + '"]').removeClass("is-invalid");
        }
    }
}

function ResetFormDvm(form_id,arrFields) {
    for(var field of arrFields){
        if(field.control_type == "Dvm"){
            $('#' + form_id).find('[form_control="' + field.form_control + '"]').addClass("d-none");
            $('#' + form_id).find('[form_control="' + field.parent_field + '"]').removeClass("is-valid");
            $('#' + form_id).find('[form_control="' + field.parent_field + '"]').removeClass("is-invalid");
        }
    }
}

//Генерация таблицы
function DataToWebTableBody(TableData,arrRowFields) {
    var strTableBodyData = ""
    if(TableData != null) {
        for (var rowData of TableData) {
            strTableBodyData += `<tr rowId="${rowData.id}">`;
            for (var rowField of arrRowFields) {
                if (rowField.cell_type == "field") {
                    if(rowField.cell_control=="amount"){//Отдельное условия для суммы операции
                        if(rowData["type"]=="Приход"){
                            strTableBodyData += `<td style="color:green;"><i class="bx bx-plus"></i>${rowData[rowField.cell_control]} <i class="bx bx-ruble text-muted" style="font-size: 12px;"></i></td>`;
                        }
                        if(rowData["type"]=="Расход"){
                            strTableBodyData += `<td style="color:red;"><i class="bx bx-minus"></i>${rowData[rowField.cell_control]} <i class="bx bx-ruble text-muted" style="font-size: 12px;"></i></td>`;
                        }
                        if(rowData["type"]=="Перевод"){
                            strTableBodyData += `<td style="color:gray;"><i class="bx bx-transfer-alt"></i>${rowData[rowField.cell_control]} <i class="bx bx-ruble text-muted" style="font-size: 12px;"></i></td>`;
                        }
                    }else{
                        strTableBodyData += `<td>${rowData[rowField.cell_control]}</td>`;
                    }
                }
                if (rowField.cell_type == "controlAccIn"){
                    //debugger;
                    let valueCodCountragent = {buyer:"Покупатель",supplier:"Поставщик",customer:"Заказчик",contractor:"Подрядчик"};
                    let valueCodEmployee = {GenMng:"Генеральный директор",Cash:"Касса",Bookkeeper:"Бухгалтер",Specialist:"Специалист",Driver:"Водитель"};
                    let strAccountDesc = "";
                    if(rowData["type"]=="Расход"){
                        //strAccountDesc = rowData["accountInDsc"];
                        strAccountDesc = valueCodCountragent[rowData["accountInDsc"]];
                    }else{
                        strAccountDesc = rowData["accountInDsc"];
                        //strAccountDesc = valueCodEmployee[rowData["accountInDsc"]];
                    }
                    strTableBodyData += `<td><div>${rowData["accountInName"]}</div><div>${strAccountDesc}</div><div>${rowData["accountInReq"]}</div></td>`;
                    //strTableBodyData += `<td><div>${strAccountDesc}</div><div>${rowData["accountInName"]}</div><div>${rowData["accountInReq"]}</div></td>`;
                }
                if (rowField.cell_type == "controlAccOut"){
                     //debugger;
                     let valueCodCountragent = {buyer:"Покупатель",supplier:"Поставщик",customer:"Заказчик",contractor:"Подрядчик"};
                     let valueCodEmployee = {GenMng:"Генеральный директор",Cash:"Касса",Bookkeeper:"Бухгалтер",Specialist:"Специалист",Driver:"Водитель"};
                     let strAccountDesc = "";
                     if(rowData["type"]=="Расход"){
                         strAccountDesc = rowData["accountOutDsc"];
                         //strAccountDesc = valueCodEmployee[rowData["accountOutDsc"]];
                     }else{
                         //strAccountDesc = rowData["accountOutDsc"]
                         strAccountDesc = valueCodCountragent[rowData["accountOutDsc"]];
                     }
                     strTableBodyData += `<td><div>${rowData["accountOutName"]}</div><div>${strAccountDesc}</div><div>${rowData["accountOutReq"]}</div></td>`;
                     //strTableBodyData += `<td><div>${strAccountDesc}</div><div>${rowData["accountOutName"]}</div><div>${rowData["accountOutReq"]}</div></td>`;
                 }
                if (rowField.cell_type == "checkBox") {
                    if (rowData[rowField.cell_control]) {
                        strTableBodyData += `<td><input class="form-check-input" field="${rowField.cell_control}" type="checkbox" checked></td>`;
                    } else {
                        strTableBodyData += `<td><input class="form-check-input" field="${rowField.cell_control}" type="checkbox"></td>`;
                    }
                }
                if (rowField.cell_type == "pickList") {
                    strTableBodyData += `<td>${rowField.value_code[rowData[rowField.cell_control]]}</td>`;
                }
            }
            strTableBodyData += `</tr>`;
        }
    }
    return strTableBodyData;
}

//Событие выбора строки таблицы
$(function(){
    $("table[data_table]").find("tbody").on("click", "tr", function () {
        //$("table[data_table]").find("tbody tr").each(function () { $(this).removeClass("bg-light");});
        $(this).parents("table").find("tbody tr").each(function () { $(this).removeClass("bg-light");});
        $(this).addClass("bg-light");
        SetTableAccessBySelectRow($(this).parents("table").attr("data_table"),"",$("table[data_table]").attr("view_name"));
    });
});

//Событие нажатия кнопки Вперед для таблицы
function SetTableOffsetNext(filterId) {
    var intLimit = parseInt($(filterId).find('input[form_control="limit"]').val());
    var intOffset = parseInt($(filterId).find('input[form_control="offset"]').val());
    intOffset = intOffset + intLimit;
    $(filterId).find('input[form_control="offset"]').val(intOffset);
}

//Событие нажатия кнопки Назад для таблицы
function SetTableOffsetBack(filterId) {
    var intLimit = parseInt($(filterId).find('input[form_control="limit"]').val());
    var intOffset = parseInt($(filterId).find('input[form_control="offset"]').val());
    intOffset = intOffset - intLimit;
    $(filterId).find('input[form_control="offset"]').val(intOffset);
}

//Установка доступности кнопок Удалить и Изменить в зависимости от выбора строки таблицы
function SetTableAccessBySelectRow(tableId,parentTableId,viewName) {
    var blSelectRowFlg = false;
    var blParentRowFlg = true;
    if(parentTableId != ""){
        if(GetParentIdInTable(parentTableId)==0){
            blParentRowFlg = false;
        }
    }

    if($(tableId).find("tbody").find("tr.bg-light").attr("rowid") == undefined && parentTableId == ""){
        /*
        if(parentTableId != "" && blParentRowFlg == false){
            $(tableId).find("button[tbl_create]").attr("disabled",true);
        }
        */
        $(tableId).find("button[tbl_update]").attr("disabled",true);
        $(tableId).find("button[tbl_delete]").attr("disabled",true);
        $(tableId).find("button[tbl_lock]").attr("disabled",true);
        blSelectRowFlg = false;
    }else{
        if(parentTableId != ""){
            if(GetParentIdInTable(parentTableId) == 0){
                $(tableId).find("button[tbl_create]").attr("disabled",true);
                $(tableId).find("button[tbl_update]").attr("disabled",true);
                $(tableId).find("button[tbl_delete]").attr("disabled",true);
                $(tableId).find("button[tbl_lock]").attr("disabled",true);
                blSelectRowFlg = false;
            }else{
                if($(tableId).find("tbody").find("tr.bg-light").attr("rowid") == undefined){
                    $(tableId).find("button[tbl_create]").attr("disabled",false);
                    $(tableId).find("button[tbl_update]").attr("disabled",true);
                    $(tableId).find("button[tbl_delete]").attr("disabled",true);
                    $(tableId).find("button[tbl_lock]").attr("disabled",true);
                    blSelectRowFlg = false;
                }else{
                    $(tableId).find("button[tbl_create]").attr("disabled",false);
                    $(tableId).find("button[tbl_update]").attr("disabled",false);
                    $(tableId).find("button[tbl_delete]").attr("disabled",false);
                    $(tableId).find("button[tbl_lock]").attr("disabled",false);
                    blSelectRowFlg = true;
                }
            }
        }else{
            $(tableId).find("button[tbl_update]").attr("disabled",false);
            $(tableId).find("button[tbl_delete]").attr("disabled",false);
            $(tableId).find("button[tbl_lock]").attr("disabled",false);
            blSelectRowFlg = true;
        }
    }

    //Если есть атрибут viewName делаем локальные операции для конкретного экрана
    if(viewName != undefined && viewName != ""){
        if(viewName == "userSettingView"){//Экран Настройки пользователя
            if(blSelectRowFlg){
                //
            }else{
                $("#SetUserRespFullNameId").html("Выберети пользователя");
                $("#SetUserRespButtonId").attr("disabled",true);
            }
        }
    }
}

//Установка доступности кнопок навигации таблицы
function SetTableAccessNavigation(tableId,filterId) {
    if($(tableId).find("tbody").find(">:first-child").find(">:first-child").html()=="1" || $(tableId).find("small[table_row_count]").attr("value")=="0"){
        $(tableId).find("button[tbl_navigation_back]").attr("disabled",true);
    }else{
        $(tableId).find("button[tbl_navigation_back]").attr("disabled",false);
    }

    if($(tableId).find("small[table_row_count]").attr("value")=="0" ||parseInt($(tableId).find('input[form_control="offset"]').val())>(parseInt($(tableId).find("small[table_row_count]").attr("value"))-parseInt($(tableId).find('input[form_control="limit"]').val()))|| parseInt($(filterId).find('input[form_control="offset"]').val()) >= (parseInt($(tableId).find("small[table_row_count]").attr("value"))-parseInt($(filterId).find('input[form_control="limit"]').val()))){
        $(tableId).find("button[tbl_navigation_next]").attr("disabled",true);
    }else{
        $(tableId).find("button[tbl_navigation_next]").attr("disabled",false);
    }
}

//Установка доступности кнопок Удалить выбранное
function SetTableAccessDeleteSelected(tableId,selectedCount) {
    if(parseInt(selectedCount)>0){
        $(tableId).find("button[tbl_delete_selected]").attr("disabled",false);
    }else{
        $(tableId).find("button[tbl_delete_selected]").attr("disabled",true);
    }
}

//Установка флага наличия фильтра
function SetFilterInitFlg(formId) {
    $("#"+formId).find("input[form_control='initFlg']").prop("checked", true);
}

//Сброс флага наличия фильтра
function ResetFilterInitFlg(formId) {
    $("#"+formId).find("input[form_control='initFlg']").prop("checked", false);
    $("#"+formId).find("input[form_control='orderLevel']").val("");
    $("#"+formId).find("input[form_control='orderBy']").val("");
}

//Установка order фильтра
function SetFilterOrder(formId,fieldName) {
    var strOrderLevel = $("#"+formId).find("input[form_control='orderLevel']").val();
    $("#"+formId).find("input[form_control='orderBy']").val(fieldName);
    if(strOrderLevel == ""){
        $("#"+formId).find("input[form_control='orderLevel']").val("DESC");
    }else{
        if(strOrderLevel == "DESC"){
            $("#"+formId).find("input[form_control='orderLevel']").val("ASC");
        }else{
            $("#"+formId).find("input[form_control='orderLevel']").val("DESC");
        }
    }
}

//Получение Id родительской записи из таблицы
function GetParentIdInTable(tableId){
    let RowId = 0;
    //Если строка не выделена берем 1ю строку
    if($(tableId).find("tbody").find("tr.bg-light").attr("rowid") == undefined)
    {
        if($(tableId).find("tbody").find(">:first-child").attr("rowid") == undefined){//Если строк совсем нет
            RowId = 0;
        }else{
            RowId = $(tableId).find("tbody").find(">:first-child").attr("rowid");
        }
    }else{
        RowId = $(tableId).find("tbody").find("tr.bg-light").attr("rowid");
    }
    return RowId;
}

//------------------------Filter----------------------------
//Получение вормы фильтра
function GetFilerData(formId,arrFieldsName){
    var requestFilter = {};
    requestFilter = GetFormData(formId,GetArrFields(arrFieldsName));
    requestFilter["filterInitFlg"]=GetFilterInit(requestFilter,GetArrFields(arrFieldsName));
    return requestFilter;
}
//Получение инициализации фильтра
function GetFilterInit(data,arrFields){
    initFlg = false;
    for(var field of arrFields){
        if(field.control_type == "pickList") {
            if(data[field.form_control]!="Default" && data[field.form_control]!="0"){
                initFlg = true;
            }
        }
    }
    return initFlg;
}
//------------------------Filter----------------------------


function SpinnerOn(boxId,description) {//Сейчас без спиннера, просто флаг загрузки для зависимых ajax
    $(boxId).find('[form_control="uploaded"]').val("N")//Признак начала прогрузки таблицы
};
function SpinnerOff(boxId,description) {//Сейчас без спиннера, просто флаг загрузки для зависимых ajax
    $(boxId).find('[form_control="uploaded"]').val("Y")//Признак начала прогрузки таблицы
};
function SpinnerStatus(boxId) {//Получить статус загрузки
    return $(boxId).find('[form_control="uploaded"]').val()//Признак начала прогрузки таблицы
};

//=========================================================
//Spinner On
/*
function SpinnerOn(AjaxChainName) {
    $("#spinner_ajax_chain").addClass(AjaxChainName);
    $("#spinner_main_div").removeClass('d-none');
};
*/
//Spinner Off
/*Чтобы функция не падала на split не забудь включиь фрагмент спиннера на html шаблоне*/
/*
function SpinnerOff(AjaxChainName) {
    $("#spinner_ajax_chain").removeClass(AjaxChainName);
    if($('#spinner_ajax_chain').attr('class').split(/\s+/).length == 1){

        $("#spinner_main_div").addClass('d-none');
    }
};
*/

/*Header*/
$(function(){
    const dropdowns = document.querySelectorAll('.header-menu-dropdown');
    dropdowns.forEach(dropdown => {
        const select = dropdown.querySelector('.select');
        const caret = dropdown.querySelector('.caret');
        const menu = dropdown.querySelector('.header-menu');
        const options = dropdown.querySelector('.menu li a');
        const selected = dropdown.querySelector('.selected');

        select.addEventListener('click', () => {
            select.classList.toggle('select-clicked');
            caret.classList.toggle('caret-rotate');
            menu.classList.toggle('menu-open');
        });

        /*
        options.forEach(option => {
            option.addEventListener('click', () => {
                selected.innerText = option.innerText;
                select.classList.remove('select-clicked');
                caret.classList.remove('caret-rotate');
                menu.classList.remove('menu-open');
                options.forEach(option => {
                    option.classList.remove('header-menu-li-active');
                });
                option.classList.add('header-menu-li-active');
            });
        });
        */
    });
});
