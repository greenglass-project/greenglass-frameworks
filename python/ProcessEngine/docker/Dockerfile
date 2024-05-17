# temp stage
FROM python:3.10-slim as builder

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc

RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

COPY requirements.txt .
RUN pip install -r requirements.txt


# final stage
FROM python:3.10-slim

COPY --from=builder /opt/venv /opt/ve
WORKDIR /app

ENV PATH="/opt/venv/bin:$PATH"
