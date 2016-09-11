This is the router that directs each VITAL user to their dedicated development and deployment environment.

#### SYSTEM REQUIREMENTS

    [@ Debian]
	curl -sL https://deb.nodesource.com/setup_6.x | bash -
	apt-get install -y nodejs
    apt-get install -y npm
    npm install -g forever

#### DEPENDENCIES

    cd vital-development-tools-router
    npm install

#### CONFIGURE

Change `config.js`.

#### RUN

    forever start app.js
