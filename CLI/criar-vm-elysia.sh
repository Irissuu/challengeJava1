#!/bin/bash

RESOURCE_GROUP="rg-docker"
LOCATION="eastus2"
VM_NAME="vm-elysia"
IMAGE="Debian:debian-11:11:latest"
SIZE="Standard_D2s_v3"
ADMIN_USERNAME="elysia"
ADMIN_PASSWORD="Elysi@a0b4vk3"
DISK_SKU="StandardSSD_LRS"
PORT_RDP=3389
PORT_API=8080
PORT_SSH=22
SHUTDOWN_TIME="0230"

az group create --name $RESOURCE_GROUP --location $LOCATION

az vm create \
  --resource-group $RESOURCE_GROUP \
  --name $VM_NAME \
  --image $IMAGE \
  --size $SIZE \
  --authentication-type password \
  --admin-username $ADMIN_USERNAME \
  --admin-password $ADMIN_PASSWORD \
  --storage-sku $DISK_SKU \
  --public-ip-sku Basic

az vm open-port --port $PORT_SSH --priority 310 --resource-group $RESOURCE_GROUP --name $VM_NAME
az vm open-port --port $PORT_API --priority 320 --resource-group $RESOURCE_GROUP --name $VM_NAME

az vm auto-shutdown \
  --resource-group $RESOURCE_GROUP \
  --name $VM_NAME \
  --time $SHUTDOWN_TIME

echo "VM provisionada e portas liberadas!"