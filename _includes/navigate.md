navigate:
{% if include.prevTitle %}
- prev: [{{ include.prevTitle }}]({{ include.prevLink }}){% endif %}{% if include.upTitle %}
- up: [{{ include.upTitle }}]({{ include.upLink }}){% endif %}{% if include.nextTitle %}
- next: [{{ include.nextTitle }}]({{ include.nextLink }}){% endif %}
