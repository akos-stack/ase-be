# Docker

**NOTE: This is not used yet!**

### Docker installation Ubuntu

```
sudo apt-get update
```

```
sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
```

```
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```

```
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
```

```
sudo apt-get update
```

```
sudo apt-get install docker-ce
```

### Docker Compose Installation Ubuntu

```
sudo curl -L https://github.com/docker/compose/releases/download/1.21.2/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
```

```
sudo chmod +x /usr/local/bin/docker-compose
```

### Docker installation MACOS

```
brew install docker docker-compose docker-machine
```

```
docker-machine create default
```

```
eval $(docker-machine env default)
```

The following fixes the virual machine ip so we can map it in hosts file:

```
echo "ifconfig eth1 192.168.99.100 netmask 255.255.255.0 broadcast 192.168.99.255 up" | docker-machine ssh default sudo tee /var/lib/boot2docker/bootsync.sh > /dev/null
```

```
docker-machine stop default
```

```
docker-machine start default
```

```
docker-machine regenerate-certs default
```

### Setup hosts file MACOS

```
echo '192.168.99.100 docker-local' | sudo tee -a /etc/hosts
```

### Setup hosts file Ubuntu

```
echo '127.0.0.1 docker-local' | sudo tee -a /etc/hosts
```

### Environment setup

Docker compose sets up all services needed for spring boot to run.

```
docker-compose up
```

---

[<: README](../README.md)