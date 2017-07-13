from django.shortcuts import render

from django.http import HttpResponse
from django.core import serializers
from .models import Voter, Election, Vote

# Create your views here.

def checkTui(request, user_id, election_id, tui_number):
    query = Vote.objects.filter(voter__nroTui = tui_number)
    if not query:
        q = Voter.objects.get(nroTui = tui_number)
        data = serializers.serialize('json', [q], fields=('fullName','lastName','rut','department'))
    else:
        data = serializers.serialize('json', Vote.objects.filter(voter__nroTui = tui_number))
    return HttpResponse(data)

def getFolio(request, user_id, election_id, tui_number, folio):
    return HttpResponse("Ingreso folio %s" % folio )