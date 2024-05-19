# temp stage
FROM python:3.10-alpine

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

ARG root

COPY dist/processengine-0.1.0-py3-none-any.whl .
RUN pip install processengine-0.1.0-py3-none-any.whl

CMD python -m processengine.engine

