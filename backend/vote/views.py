from django.shortcuts import render

from django.http import HttpResponse
from django.core import serializers
from .models import Voter, Election, Vote
import json

# Create your views here.

def checkTui(request, user_id, election_id, tui_number):
    query = Vote.objects.filter(voter__nroTui = tui_number)
    if not query:
        q = Voter.objects.get(nroTui = tui_number)
        data = serializers.serialize('json', [q])
        tmp = json.loads(data)
        data = json.dumps(tmp[0]['fields'])
    else:
        data = serializers.serialize('json', Vote.objects.filter(voter__nroTui = tui_number))
    return HttpResponse(data, content_type='application/json')

def checkRut(request, user_id, election_id, rut_number):
    query = Vote.objects.filter(voter__rut = rut_number)
    if not query:
        q = Voter.objects.get(rut = rut_number)
        data = serializers.serialize('json', [q])
        tmp = json.loads(data)
        data = json.dumps(tmp[0]['fields'])
    else:
        data = serializers.serialize('json', Vote.objects.filter(voter__rut = rut_number))
    return HttpResponse(data, content_type='application/json')

def getFolio(request, user_id, election_id, rut_number, folio):
    voterpk = Voter.objects.filter(rut = rut_number)[0]
    electionpk = Election.objects.filter(pk = election_id)[0]
    newVote = Vote(election=electionpk, voter=voterpk, roster=folio, voteMethod='tui')
    newVote.save()
    return HttpResponse("{\"Success\":\"True\"}", content_type='application/json')