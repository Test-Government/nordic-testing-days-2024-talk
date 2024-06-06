# Petclinic

```shell
cd petclinic
docker compose up -d
```

Open http://specs.local.teststuff.net:8000/customers/ in your browser.

Open tests in your favorite IDE(IntelliJ) and run tests, reset data with http://testdata.local.teststuff.net:8085/reset

| Description          | URL                                                 |
|----------------------|-----------------------------------------------------|
| Local frontend       | http://petclinic.local.teststuff.net:8080/#!/owners |
| Specifications       | http://specs.local.teststuff.net:8000/              |
| Requirement snippets | http://snippets.local.teststuff.net:8001/           |
| Test reports         | http://ci.local.teststuff.net:8002/                 |
| Test data            | http://testdata.local.teststuff.net:8085/           |
| Reset test data      | http://testdata.local.teststuff.net:8085/reset      |

# Presentation

Images are supported and will render in your terminal as long as it supports either the iterm2 image protocol, the kitty
graphics protocol, or sixel. Some of the terminals where at least one of these is supported are:

* kitty
* iterm2
* wezterm
* foot

Run the following command to view the presentation in your terminal:

```shell
docker run --rm -it -e TERM="$TERM" ghcr.io/test-government/nordic-testing-days-2024-talk:main  
```

Windows users can view the PDF or ping Leonard to work on issues:

* https://github.com/microsoft/terminal/issues/5746
* https://github.com/microsoft/terminal/issues/8389
* https://github.com/microsoft/terminal/issues/448
