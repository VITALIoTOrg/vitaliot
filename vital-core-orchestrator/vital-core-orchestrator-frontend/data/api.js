var serviceAPI = {
    'GET /service': {
        response: [
            {
                'id': 1,
                'status': 'DISABLED',
                'access': 'PRIVATE',
                'workflowScript': ''
            }
        ]
    },

    'GET /service/{id}': {
        response: {
            'id': 1,
            'status': 'DISABLED',
            'access': 'PRIVATE',
            'workflowScript': 'function execute(input) {\n' +
            '\tvar output;\n' +
            '\n' +
            '\t/*** Insert you code here ***/\n' +
            '\t \n' +
            '\t \n' +
            '\t \n  ' +
            '\t/*** End: Insert you code here ***/\n' +
            '\n' +
            '\treturn output;\n' +
            '}\n'
        }
    },

    'POST /service': {
        request: {
            'name': 'A name',
            'description': 'A Description',
            'status': 'DISABLED',
            'access': 'PRIVATE',
            'workflowScript': ''
        },
        response: {
            'id': 1,
            'name': 'A name',
            'description': 'A Description',
            'status': 'DISABLED',
            'access': 'PRIVATE',
            'workflowScript': ''
        }
    },

    'PUT /service/{id}': {
        request: {
            'id': 1,
            'name': 'A name',
            'description': 'A Description',
            'status': 'DISABLED',
            'access': 'PRIVATE',
            'workflowScript': ''
        },
        response: {
            'id': 1,
            'name': 'A name',
            'description': 'A Description',
            'status': 'DISABLED',
            'access': 'PRIVATE',
            'workflowScript': ''
        }
    }
};

var executionAPI = {
    'POST /service/execute': {
        request: {
            workflowScript: '',
            input: {} // An object with input data
        },
        response: {} // An object with result of execution
    },

    'PUT /service/execute/{id}': {
        request: {}, // An object with input data
        response: {} // An object with result of execution
    },
};
