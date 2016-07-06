$(function() {

	// Get all registered IoT systems.
	get();

	$('#basic-auth').on('change', function() {
		if ($('#basic-auth').is(':checked')) {
			$('#username').parent('.form-group').show();
			$('#password').parent('.form-group').show();
		} else {
			$('#username').parent('.form-group').hide();
			$('#password').parent('.form-group').hide();
		}
	});
});

function get() {
	$
			.get(
					"/vital-core-iot-data-adapter/rest/systems?x=" + new Date().getTime(),
					function(data) {
						var table = $('#table');
						table.find('tbody tr').remove();
						for (var i = 0; i < data.length; i++) {
							var iotsystem = data[i];
							var tr = $('<tr id="'
									+ iotsystem.id
									+ '" class="'
									+ (iotsystem.enabled ? "enabled"
											: "disabled") + '" />');
							$('<td/>').text(i + 1).appendTo(tr);
							$('<td/>').text(iotsystem.name).appendTo(tr);
							$('<td/>').text(iotsystem.uri).appendTo(tr);
							var slastDataRefresh = iotsystem.lastDataRefresh ? iotsystem.lastDataRefresh : "-";
							var slastMetadataRefresh = iotsystem.lastMetadataRefresh ? iotsystem.lastMetadataRefresh : "-";
							// Alternative colour: #749ba4.
							$('<td style="font-size: 12px; color: #f64e5d"/>')
									.html(
											"Last metadata refresh: "
													+ slastMetadataRefresh
													+ "<br/>Last data refresh: "
													+ slastDataRefresh)
									.appendTo(tr);
							$('<td/>')
									.html(
											'<button type="button" class="btn btn-default btn-block refresh-button" onClick="refresh(\''
													+ iotsystem.id
													+ '\')")>Refresh</button>')
									.appendTo(tr);
							$('<td/>').html(
									'<button type="button" class="btn btn-default btn-block" onClick="edit(\''
											+ iotsystem.id
											+ '\')">Edit</button>')
									.appendTo(tr);
							$('<td/>')
									.html(
											'<button type="button" class="btn btn-default btn-block" onClick="deregister(\''
													+ iotsystem.id
													+ '\')">Deregister</button>')
									.appendTo(tr);
							tr.find('.refresh-button').prop('disabled',
									!iotsystem.enabled);
							tr.appendTo(table);
						}
					}, "json");
}

function refresh(id) {
	$.ajax({
		url : '/vital-core-iot-data-adapter/rest/systems/' + id + '/refresh',
		type : 'GET',
		complete : function(jqxhr, status) {
			// NOTE: Give it some time.
			setTimeout(get, 3000);
		}
	});
}

function edit(id) {
	$.ajax({
		url : '/vital-core-iot-data-adapter/rest/systems/' + id,
		type : 'GET',
		success : function(data) {
			// NOTE: Give it some time.
			$('#id').val(data.id);
			$('#name').val(data.name);
			$('#uri').val(data.uri);
			$('#ppi').val(data.ppi);
			$('#refresh-period').val(data.refreshPeriod);
			$('#register').text('Update');
			$('#enabled').prop('checked', data.enabled);
			$('#basic-auth').prop('checked', data.authenticationInfo.username);
			$('#username').val(data.authenticationInfo.username);
			$('#password').val(data.authenticationInfo.password);
			if (data.authenticationInfo.username) {
				$('#username').parent('.form-group').show();
				$('#password').parent('.form-group').show();
			} else {
				$('#username').parent('.form-group').hide();
				$('#password').parent('.form-group').hide();
			}
			$('#label').text('Edit IoT system');
			$('#modal').modal('show');
		}
	});
}

function save() {
	var id = $('#id').val();
	if (id)
		update();
	else
		register();
}

function deregister(id) {
	$.ajax({
		url : '/vital-core-iot-data-adapter/rest/systems/' + id,
		type : 'DELETE',
		complete : function(jqxhr, status) {
			// NOTE: Give it some time.
			setTimeout(get, 3000);
		}
	});
}

function register() {
	var data = {};
	data.name = $('#name').val();
	data.uri = $('#uri').val();
	data.ppi = $('#ppi').val();
	data.refreshPeriod = $('#refresh-period').val();
	data.enabled = $('#enabled').is(':checked');
	data.authenticationInfo = {};
	if ($('#basic-auth').is(':checked')) {
		data.authenticationInfo.username = $('#username').val();
		data.authenticationInfo.password = $('#password').val();
	}
	$.ajax({
		url : '/vital-core-iot-data-adapter/rest/systems',
		type : 'PUT',
		contentType : 'application/json',
		data : JSON.stringify(data),
		complete : function(jqxhr, status) {
			$('#modal').modal('hide');
			// NOTE: Give it some time.
			setTimeout(get, 3000);
		}
	});
}

function update() {
	var data = {};
	data.id = $('#id').val();
	data.name = $('#name').val();
	data.uri = $('#uri').val();
	data.ppi = $('#ppi').val();
	data.refreshPeriod = $('#refresh-period').val();
	data.enabled = $('#enabled').is(':checked');
	data.authenticationInfo = {};
	if ($('#basic-auth').is(':checked')) {
		data.authenticationInfo.username = $('#username').val();
		data.authenticationInfo.password = $('#password').val();
	}
	$.ajax({
		url : '/vital-core-iot-data-adapter/rest/systems/' + data.id,
		type : 'POST',
		contentType : 'application/json',
		data : JSON.stringify(data),
		complete : function(jqxhr, status) {
			$('#modal').modal('hide');
			// NOTE: Give it some time.
			setTimeout(get, 3000);
		}
	});
}

function create() {
	$('#id').val('');
	$('#name').val('');
	$('#uri').val('');
	$('#ppi').val('');
	$('#name').val('');
	$('#refresh-period').val(10);
	$('#register').text('Register');
	$('#enabled').prop('checked', false);
	$('#basic-auth').prop('checked', false);
	$('#username').val('');
	$('#username').parent('.form-group').hide();
	$('#password').val('');
	$('#password').parent('.form-group').hide();
	$('#label').text('Register IoT system');
	$('#modal').modal('show');
}

function toDate(s) {
	var d = s ? new Date(s) : null;
	if (!d)
		return "-";
	var date = (d.getDate() < 10 ? '0' : '') + d.getDate();
	var month = (d.getMonth() + 1 < 10 ? '0' : '') + (d.getMonth() + 1);
	var hours = (d.getHours() < 10 ? '0' : '') + d.getHours();
	var minutes = (d.getMinutes() < 10 ? '0' : '') + d.getMinutes();
	return d ? date + "." + month + "." + d.getFullYear() + " " + hours + ":"
			+ minutes : "-";
}